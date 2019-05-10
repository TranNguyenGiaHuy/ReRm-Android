package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.Room
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.ImageController
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.opensooq.pluto.PlutoView
import com.opensooq.pluto.listeners.OnItemClickListener
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_room.view.*
import java.io.File
import javax.inject.Inject

class PostAdapter(val items : ArrayList<RoomData>, val context: Context, val avatarController: AvatarController, val imageController: ImageController) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

//    @Inject
//    lateinit var avatarController: AvatarController

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room : RoomData = items[position]
        holder.tvPrice.text = room.price.toString()
        holder.tvAddress.text = room.address
        holder.tvDescription.text = room.description
        holder.tvPostUserName.text = room.ownerName

        if (room.ownerAvatar == null) {
            avatarController.getAvatarOfUser(room.ownerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<File> {
                    override fun onSuccess(t: File) {
                        room.ownerAvatar = t
                        notifyItemChanged(position)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
        } else {
            room.ownerAvatar?.let {
                Glide
                    .with(holder.imgPostProfile)
                    .load(room.ownerAvatar)
                    .into(holder.imgPostProfile)
            }
        }

        if (room.imageList == null) {
            imageController.getAllImageOfRoom(room.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object: SingleObserver<List<File>> {

                    override fun onSuccess(t: List<File>) {
                        room.imageList = t
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
        } else {
            room.imageList?.let {
                if (it.isNotEmpty()) {
                    val adapter = ImageViewAdapter(
                        it,
                        OnItemClickListener { item, position -> },
                        object: ImageViewAdapter.Listener {

                            override fun doubleClickListener() {
                                holder.btnSave.setBackgroundColor(R.color.red_btn_bg_color)
                            }

                        }
                    )

                    holder.imgViewer.create(adapter)
                }
            }
        }

        holder.itemView.setOnClickListener{
            TransactionManager.replaceFragmentWithWithBackStack(
                context,
                RoomDetailFragment()
            )
        }
    }
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomType : TextView
        val tvPrice : TextView
        val tvAddress : TextView
        val tvDescription : TextView
        val tvPostUserName: TextView
        val imgPostProfile: ImageView
        val imgViewer: PlutoView
        val btnSave: ImageButton

        init {
            tvRoomType = view.findViewById(R.id.tv_room_type)
            tvPrice = view.findViewById(R.id.tv_post_price)
            tvAddress = view.findViewById(R.id.tv_post_location)
            tvDescription = view.findViewById(R.id.tv_post_description)
            tvPostUserName = view.findViewById(R.id.tv_post_username)
            imgPostProfile = view.findViewById(R.id.img_post_profile)
            imgViewer = view.findViewById(R.id.imgViewer)
            btnSave = view.findViewById(R.id.btn_save)
        }
    }
}

