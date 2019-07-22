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
import com.huytran.rermandroid.data.local.dao.NotificationDAO
import com.huytran.rermandroid.data.local.entity.Message
import com.huytran.rermandroid.data.local.entity.Notification
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.MessageRepository
import com.huytran.rermandroid.data.local.repository.NotificationRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.utilities.AppConstants
import com.huytran.rermandroid.utilities.UtilityFunctions
import dagger.android.AndroidInjection
import io.reactivex.Completable
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
    @Inject
    lateinit var roomController: RoomController
    @Inject
    lateinit var userController: UserController
    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val CHANNEL_ID = "100001"

    data class ExtraData(val from: Long, val roomId: Long, val value: Long, val tsStart: Long?, val tsEnd: Long?)

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)

        p0?.let {
            val notificationType = AppConstants.NotificationType.toRoomType(it.data["notificationType"]?.toIntOrNull())
            val extraData = it.data["extraData"]

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
                        .subscribe(object : SingleObserver<File> {
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
                AppConstants.NotificationType.MESSAGE_TYPE_RENT_REQUEST,
                AppConstants.NotificationType.MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_OWNER,
                AppConstants.NotificationType.MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_RENTER,
                AppConstants.NotificationType.MESSAGE_TYPE_OWNER_ACCEPTED_ANOTHER_REQUEST,
                AppConstants.NotificationType.MESSAGE_TYPE_RENT_SUCCESS,
                AppConstants.NotificationType.MESSAGE_TYPE_CONTRACT_TERMINATE,
                AppConstants.NotificationType.MESSAGE_TYPE_ADD_PAYMENT,
                AppConstants.NotificationType.MESSAGE_TYPE_BILL,
                AppConstants.NotificationType.MESSAGE_TYPE_PAYMENT_REQUEST,
                AppConstants.NotificationType.MESSAGE_TYPE_CONFIRM_PAYMENT -> {
                    extraData?.let {data ->
                        saveAndPushNotification(data, notificationType)
                    }
//                    saveAndPushNotification(it.data["from"]!!.toLong(), it.data["room"]!!.toLong(), it.data["value"]!!.toLong(), notificationType)
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

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
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
                NotificationChannel(CHANNEL_ID, "Rent Notification", NotificationManager.IMPORTANCE_DEFAULT)

            //Configure Notification Channel
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)

            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setChannelId(CHANNEL_ID)
        }

        notificationManager.notify(100, notificationBuilder.build())


    }

    private fun extractExtraData(extraData: String): ExtraData? {
        val raw = extraData.substring(1, extraData.length - 1)
        val dataPairs = raw.split(",")
        if (dataPairs.size != 3) return null
        var from: Long? = null
        var room: Long? = null
        var value: Long? = null
        var tsStart: Long? = null
        var tsEnd: Long? = null

        dataPairs.forEach { dataPair ->
            val key = dataPair.split(":")[0].trim().replace("\"", "")
            val data = dataPair.split(":")[1]
            when (key) {
                "from" -> from = data.toLong()
                "room" -> room = data.toLong()
                "value" -> value = data.toLong()
                "tsStart" -> tsStart = data.toLong()
                "tsEnd" -> tsEnd = data.toLong()
            }
        }

        return ExtraData(
            from!!,
            room!!,
            value!!,
            tsStart,
            tsEnd
        )
    }

    private fun extractExtraDataAndSaveNotification(extraData: String): ExtraData? {
        val data = extractExtraData(extraData) ?: return null
        notificationRepository.insert(
            Notification(
                tsCreated = System.currentTimeMillis(),
                from = data.from,
                roomId = data.roomId,
                value = data.value,
                isSeen = false,
                message = ""
            )
        )
        return data
    }

    private fun saveAndPushNotification(extraData: String, type: AppConstants.NotificationType) {
        val data = extractExtraData(extraData) ?: return

        var title: String
        var body: String
        var roomName = ""
        var fromName = ""

        roomController.getRoom(data.roomId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMap { room ->
                roomName = room.title
                userController.getInfoOfUser(data.from)
            }.flatMapCompletable { user ->
                fromName = user.userName
                Completable.complete()
            }
            .doOnComplete {
                when (type) {
                    AppConstants.NotificationType.MESSAGE_TYPE_RENT_REQUEST -> {
                        title = "Rent Request!"
                        body = "$fromName want rent your $roomName."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_OWNER -> {
                        title = "Rent Request Has Been Cancelled!"
                        body = "Rent request for $roomName of $fromName has been cancelled."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_CANCEL_RENT_REQUEST_TO_RENTER -> {
                        title = "Rent Request Has Been Cancelled!"
                        body = "Your rent request for $roomName has been cancelled by the owner."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_OWNER_ACCEPTED_ANOTHER_REQUEST -> {
                        title = "Rent Request Has Been Cancelled!"
                        body = "Your rent request for $roomName has been cancelled because the owner has accepted another request."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_RENT_SUCCESS -> {
                        title = "Rent Success!"
                        body = "You have succeeded rent $roomName. Now you can move to this room."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_CONTRACT_TERMINATE -> {
                        title = "Contract Terminate!"
                        body = "Contract for $roomName has been terminate."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_ADD_PAYMENT -> {
                        title = "Add Payment!"
                        body = "Please add bill for $roomName" +
                                "${if (data.tsStart != null
                                    && data.tsEnd != null)
                                    " from ${UtilityFunctions.timestampToString(data.tsStart)} " +
                                            "to ${UtilityFunctions.timestampToString(data.tsEnd)}"
                                else ""}."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_BILL -> {
                        title = "You Have A Bill!"
                        body = "The bill for your $roomName" +
                                "${if (data.tsStart != null
                                    && data.tsEnd != null)
                                    " from ${UtilityFunctions.timestampToString(data.tsStart)} " +
                                            "to ${UtilityFunctions.timestampToString(data.tsEnd)}"
                                else ""} has value: ${data.value} VND."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_PAYMENT_REQUEST -> {
                        title = "Payment Request!"
                        body = "Have you receive the money for $roomName" +
                                "${if (data.tsStart != null
                                    && data.tsEnd != null)
                                    " from ${UtilityFunctions.timestampToString(data.tsStart)} " +
                                            "to ${UtilityFunctions.timestampToString(data.tsEnd)}"
                                else ""} from $fromName, which has value: ${data.value} VND."
                    }
                    AppConstants.NotificationType.MESSAGE_TYPE_CONFIRM_PAYMENT -> {
                        title = "Payment Confirmation"
                        body = "$fromName has confirmed your payment for $roomName" +
                                "${if (data.tsStart != null
                                    && data.tsEnd != null)
                                    " from ${UtilityFunctions.timestampToString(data.tsStart)} " +
                                            "to ${UtilityFunctions.timestampToString(data.tsEnd)}"
                                else ""}."
                    }
                    else -> return@doOnComplete
                }

                notificationRepository.insert(
                    Notification(
                        tsCreated = System.currentTimeMillis(),
                        from = data.from,
                        roomId = data.roomId,
                        value = data.value,
                        isSeen = false,
                        message = body
                    )
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe()

                sendNotification(
                    title,
                    body
                )
            }
            .subscribe()

    }

}