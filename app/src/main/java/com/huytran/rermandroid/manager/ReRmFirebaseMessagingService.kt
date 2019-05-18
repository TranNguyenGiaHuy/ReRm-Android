package com.huytran.rermandroid.manager

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.huytran.rermandroid.data.remote.MessageController
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class ReRmFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var messageController: MessageController

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
    }

    override fun onNewToken(p0: String?) {
        p0?.let {
            messageController.addToken(it)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .doOnError { throwable ->
                    throwable.printStackTrace()
                }
                .subscribe()
        }
    }

}