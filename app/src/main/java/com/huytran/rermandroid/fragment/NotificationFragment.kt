package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.NotificationAdapter
import com.huytran.rermandroid.data.local.entity.Notification
import com.huytran.rermandroid.data.local.repository.NotificationRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.ImageController
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject

class NotificationFragment : BaseFragment() {

    @Inject
    lateinit var notificationRepository: NotificationRepository
    @Inject
    lateinit var avatarController: AvatarController
    @Inject
    lateinit var roomController: RoomController
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var imageController: ImageController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvNotification.layoutManager = LinearLayoutManager(activity)
        notificationRepository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object : SingleObserver<List<Notification>> {
                override fun onSuccess(t: List<Notification>) {
                    rvNotification.apply {
                        rvNotification.adapter = NotificationAdapter(
                            context!!,
                            t.sortedByDescending(Notification::tsCreated),
                            avatarController,
                            roomController,
                            userRepository,
                            imageController,
                            notificationRepository
                        )
                    }
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }
}