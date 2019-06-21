package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.User
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.dao.MessageDAO
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.Message
import com.huytran.rermandroid.data.local.localbean.ChatMessage
import com.huytran.rermandroid.data.local.localbean.ChatUser
import com.huytran.rermandroid.data.local.repository.MessageRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.MessageController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chat.*
import org.reactivestreams.Subscription
import javax.inject.Inject

class ChatFragment(private val contactId: Long) : BaseFragment(), MessagesListAdapter.OnLoadMoreListener {

    @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var userController: UserController

    @Inject
    lateinit var avatarController: AvatarController

    @Inject
    lateinit var messageController: MessageController

    @Inject
    lateinit var userDAO: UserDAO

    private var ownerId: Long? = null

    private var tsLastMessage = Long.MAX_VALUE

    private lateinit var ownerChatUser: ChatUser
    private lateinit var contactChatUser: ChatUser

    lateinit var adapter: MessagesListAdapter<ChatMessage>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ownerBuilder = ChatUser.Builder()
        val contactBuilder = ChatUser.Builder()

        // init data
        userDAO.getLastSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .flatMap { user ->
                // init adapter
                adapter = MessagesListAdapter(
                    user.svId.toString(),
                    ImageLoader { imageView, url, _ ->
                        Glide.with(this).load(url).into(imageView)
                    }
                )
                messagesList.setAdapter(adapter)

                // build owner user
                ownerId = user.svId

                ownerBuilder.id = user.svId
                ownerBuilder.name = user.userName

                avatarController.getAvatarOfUser(user.svId)
            }.flatMapCompletable { file ->
                ownerBuilder.avatarPath = file.path
                ownerChatUser = ownerBuilder.build()
                Completable.complete()
                // build contact user
            }.andThen(userController.getInfoOfUser(contactId))
            .flatMap { user ->
                contactBuilder.id = user.id
                contactBuilder.name = user.name

                avatarController.getAvatarOfUser(user.id)
            }.flatMapCompletable { file ->
                contactBuilder.avatarPath = file.path
                contactChatUser = contactBuilder.build()
                Completable.complete()
            }.subscribe(object : CompletableObserver {
                override fun onComplete() {
                    // get all message
                    messageRepository.getAllOfUserMaybe(contactId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe {
                            disposableContainer.add(it)
                        }
                        .subscribe(object : MaybeObserver<List<Message>> {
                            override fun onSuccess(t: List<Message>) {
                                if (t.isEmpty()) return
                                tsLastMessage = t.last().tsCreated
                                adapter.addToEnd(
                                    messageListToChatMessageList(t),
                                    true
                                )

                                // seen all
                                seenMessageList(t)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .doOnSubscribe {
                                        disposableContainer.add(it)
                                    }
                                    .subscribe(object : CompletableObserver {
                                        override fun onComplete() {
                                        }

                                        override fun onSubscribe(d: Disposable) {
                                        }

                                        override fun onError(e: Throwable) {
                                        }

                                    })
                            }

                            override fun onComplete() {
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                            }

                        })
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })

        // update ui
        val getLastMessageDisposable = messageRepository.getLastOfUserFlowable(contactId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy {
                if (it.tsCreated <= tsLastMessage) return@subscribeBy
                adapter.addToStart(
                    messageToChatMessage(it),
                    true
                )
            }
        getLastMessageDisposable.addTo(disposableContainer)

        // input event
        input.setInputListener { content ->
            // add message to database
            messageRepository.addMessageSingle(
                Message(
                    userId = contactId,
                    tsCreated = System.currentTimeMillis(),
                    isSeen = true,
                    isReceive = false,
                    content = content.toString()
                )
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    disposableContainer.add(it)
                }.flatMapCompletable {
                    Completable.complete()
                }.subscribe()

            // send message
            messageController.sendMessage(
                content.toString(),
                contactId
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    disposableContainer.add(it)
                }.subscribe()

            true
        }
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
    }

    private fun messageListToChatMessageList(messageList: List<Message>): List<ChatMessage> {
        return messageList.map {
            messageToChatMessage(it)
        }
    }

    private fun messageToChatMessage(message: Message): ChatMessage {
        return ChatMessage.Builder()
            .id(message.id!!)
            .user(
                if (message.isReceive) contactChatUser else ownerChatUser
            )
            .content(message.content)
            .tsCreated(message.tsCreated)
            .build()
    }

    private fun seenMessageList(messageList: List<Message>): Completable {
        return Completable.merge {
            messageList.forEach { message ->
                message.isSeen = true
                messageRepository.addMessage(
                    message
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        disposableContainer.add(it)
                    }.subscribe()
            }
        }
    }

}