package com.huytran.rermandroid.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.entity.Notification
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.local.repository.NotificationRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.ImageController
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class NotificationAdapter(
    private val context: Context,
    private val notificationList: List<Notification>,
    private val avatarController: AvatarController,
    private val roomController: RoomController,
    private val userRepository: UserRepository,
    private val imageController: ImageController
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_notification, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.tvContent.text = notification.message
        avatarController.getAvatarOfUser(notification.from)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<File> {
                override fun onSuccess(t: File) {
                    Glide.with(holder.ivAvatar).load(t).into(holder.ivAvatar)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        holder.llContainer.setBackgroundColor(
            if (notification.isSeen) Color.WHITE else Color.LTGRAY
        )

        var roomData: RoomData? = null
        var isOwner = false
        holder.llContainer.setOnClickListener {
            roomController.getRoom(notification.roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap { room ->
                    roomData = RoomData(room)
                    avatarController.getAvatarOfUser(room.ownerId)
                }.flatMap { avatar ->
                    roomData?.ownerAvatar = avatar
                    imageController.getAllImageOfRoom(roomData!!.id)
                }.flatMap { imageList ->
                    roomData?.imageList = imageList
                    userRepository.getLastSingle()
                }.flatMapCompletable { user ->
                    isOwner = user.svId == roomData!!.ownerId
                    Completable.complete()
                }.subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        roomData?.let { roomData ->
                            TransactionManager.replaceFragmentWithWithBackStack(
                                context,
                                RoomDetailFragment(roomData, isOwner)
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

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: CircleImageView = view.findViewById(R.id.ivAvatar)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val llContainer: TextView = view.findViewById(R.id.llContainer)

    }

}