package com.huytran.rermandroid.manager

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity


class ReRmFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
        p0?.let {
            sendNotification(
                it.data["title"] ?: "",
                it.data["body"] ?: ""
            )
        }
    }

    private fun sendNotification(title: String, content: String) {

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, "Rent Notification", NotificationManager.IMPORTANCE_DEFAULT)

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