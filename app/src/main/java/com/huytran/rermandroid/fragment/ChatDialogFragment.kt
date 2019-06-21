package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.dao.MessageDAO
import com.huytran.rermandroid.data.local.dao.UserDAO
import com.huytran.rermandroid.data.local.entity.Message
import com.huytran.rermandroid.data.local.localbean.ChatDialog
import com.huytran.rermandroid.data.local.localbean.ChatMessage
import com.huytran.rermandroid.data.local.localbean.ChatUser
import com.huytran.rermandroid.data.local.repository.MessageRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.MessageController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_chat_dialog.*
import org.reactivestreams.Subscription
import java.util.function.Consumer
import javax.inject.Inject

class ChatDialogFragment : BaseFragment(), DialogsListAdapter.OnDialogClickListener<ChatDialog> {

    @Inject
    lateinit var messageRepository: MessageRepository

    @Inject
    lateinit var userDAO: UserDAO

    @Inject
    lateinit var userController: UserController

    @Inject
    lateinit var avatarController: AvatarController

    private lateinit var ownerChatUser: ChatUser

    private val adapter = DialogsListAdapter<ChatDialog>(
        ImageLoader { imageView, url, _ ->
            Glide.with(this).load(url).into(imageView)
        }
    )

    override fun onDialogClick(dialog: ChatDialog?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_chat_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialogsList.setAdapter(adapter)

        // get owner user
        val ownerBuilder = ChatUser.Builder()
        userDAO.getLastSingle()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .flatMap { user ->
                ownerBuilder.id = user.svId
                ownerBuilder.name = user.userName

                avatarController.getAvatarOfUser(user.svId)
            }.flatMapCompletable { file ->
                ownerBuilder.avatarPath = file.path
                Completable.complete()
            }.subscribe(object : CompletableObserver {
                override fun onComplete() {
                    ownerChatUser = ownerBuilder.build()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })

        // get and set dialog
        val getMessageDisposable = messageRepository
            .getAllFlowable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy {
                updateAdapter(it)
            }
        getMessageDisposable.addTo(disposableContainer)

//        messageRepository
//            .getAllSingle()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe(object : SingleObserver<List<Message>> {
//                override fun onSuccess(t: List<Message>) {
//                    if (t.isNotEmpty()) {
//                        updateAdapter(t)
//                    }
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                    disposableContainer.add(d)
//                }
//
//                override fun onError(e: Throwable) {
//                }
//
//            })

        // adapter event
        adapter.setOnDialogClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                context!!,
                ChatFragment(
                    it.users.last().id.toLong()
                )
            )
        }
    }

    private fun updateAdapter(messageList: List<Message>) {
        messageList.groupBy(Message::userId).forEach {
            messageListToChatDialog(it.value)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    disposableContainer.add(it)
                }
                .subscribe(object : SingleObserver<ChatDialog> {
                    override fun onSuccess(t: ChatDialog) {
                        adapter.upsertItem(t)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }

                })
        }
    }

    private fun messageListToChatDialog(messageList: List<Message>): Single<ChatDialog> {
        val userId = messageList.first().userId
        val chatUserBuilder = ChatUser.Builder()

        return userController.getInfoOfUser(userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .flatMap { user ->
                chatUserBuilder.id = user.id
                chatUserBuilder.name = if (user.userName.isNotBlank()) user.userName else user.name

                avatarController.getAvatarOfUser(user.id)
            }.flatMap { file ->
                chatUserBuilder.avatarPath = file.path
                val chatUser = chatUserBuilder.build()

                Single.create<ChatDialog> { emitter ->
                    emitter.onSuccess(
                        ChatDialog.Builder()
                            .id(messageList.first().userId.toString())
                            .dialogPhoto(chatUser.avatar)
                            .unreadCount(messageList.count {
                                !it.isSeen
                            })
                            .lastMessage(
                                ChatMessage.Builder()
                                    .id(-1)
                                    .user(chatUser)
                                    .tsCreated(messageList.last().tsCreated)
                                    .content(messageList.last().content)
                                    .build()
                            )
                            .users(
                                mutableListOf(ownerChatUser, chatUser)
                            )
                            .dialogName(chatUser.name)
                            .build()
                    )
                }
            }
    }

}