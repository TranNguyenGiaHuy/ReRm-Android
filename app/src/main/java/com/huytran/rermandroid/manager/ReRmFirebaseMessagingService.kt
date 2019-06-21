package com.huytran.rermandroid.manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.data.local.dao.MessageDAO
import com.huytran.rermandroid.data.local.entity.Message
import com.huytran.rermandroid.data.local.repository.MessageRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.utilities.AppConstants
import dagger.android.AndroidInjection
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject


class ReRmFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var messageRepository: MessageRepository
    @Inject
    lateinit var avatarController: AvatarController

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        p0?.let {
            val notificationType = AppConstants.NotificationType.toRoomType(it.data["notificationType"]?.toIntOrNull())

            when (notificationType) {
                AppConstants.NotificationType.MESSAGE_TYPE_MESSAGE -> {

                }
                AppConstants.NotificationType.MESSAGE_TYPE_NOTIFICATION -> {

                }
                AppConstants.NotificationType.MESSAGE_TYPE_VIDEO_CALL -> {

                }
                AppConstants.NotificationType.MESSAGE_TYPE_CHAT -> {
                    val sender = it.data["sender"]?.toLongOrNull() ?: return@let
                    messageRepository.addMessage(
                        Message(
                            userId = sender,
                            content = it.data["body"] ?: "",
                            isReceive = true,
                            isSeen = false,
                            tsCreated = System.currentTimeMillis()
                        )
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe()

                    avatarController.getAvatarOfUser(sender)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(object: SingleObserver<File> {
                            override fun onSuccess(t: File) {
                                val bitmap = BitmapFactory.decodeFile(t.path)

                                sendNotification(
                                    it.data["title"] ?: "",
                                    it.data["body"] ?: "",
                                    bitmap
                                )
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                sendNotification(
                                    it.data["title"] ?: "",
                                    it.data["body"] ?: ""
                                )
                            }

                        })
                }

            }

        }
    }

    private fun sendNotification(title: String, content: String, bitmap: Bitmap? = null) {

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "100001"

        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        bitmap?.let {
            notificationBuilder.setLargeIcon(bitmap)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "Rent Notification", NotificationManager.IMPORTANCE_DEFAULT)

            //Configure Notification Channel
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setChannelId(channelId)
        }

        notificationManager.notify(100, notificationBuilder.build())


    }

}