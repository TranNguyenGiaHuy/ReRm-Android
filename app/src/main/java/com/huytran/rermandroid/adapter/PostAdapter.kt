package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.Room
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_room.view.*
import java.io.File
import javax.inject.Inject

class PostAdapter(val items : ArrayList<RoomData>, val context: Context, val avatarController: AvatarController) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

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

        room.ownerAvatar?.let {
            Glide
                .with(holder.imgPostProfile)
                .load(room.ownerAvatar)
                .into(holder.imgPostProfile)
        }

        avatarController.getAvatarOfUser(room.ownerId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
//            .doOnSubscribe {
//                disposableContainer.add(it)
//            }
            .subscribe(object : SingleObserver<File> {
                override fun onSuccess(t: File) {

//                    Glide
//                        .with(holder.imgPostProfile)
//                        .load(t)
//                        .into(holder.imgPostProfile)
                    room.ownerAvatar = t
                    notifyDataSetChanged()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

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

        init {
            tvRoomType = view.findViewById(R.id.tv_room_type)
            tvPrice = view.findViewById(R.id.tv_post_price)
            tvAddress = view.findViewById(R.id.tv_post_location)
            tvDescription = view.findViewById(R.id.tv_post_description)
            tvPostUserName = view.findViewById(R.id.tv_post_username)
            imgPostProfile = view.findViewById(R.id.img_post_profile)
        }
    }
}

