package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.RentRequest
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.RentRequestController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.utilities.UtilityFunctions
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RentRequestAdapter(
    private val requestList: List<RentRequest>,
    private val context: Context,
    private val avatarController: AvatarController,
    private val userController: UserController,
    private val rentRequestController: RentRequestController) :
    RecyclerView.Adapter<RentRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_rent_request, parent, false))
    }

    override fun getItemCount() = requestList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentRequest = requestList[position]

        val rentTime = "From ${UtilityFunctions.timestampToString(rentRequest.tsStart)} to ${UtilityFunctions.timestampToString(rentRequest.tsStart)}"
        holder.tvRentTime.text = rentTime

        userController.getInfoOfUser(rentRequest.renter)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                holder.tvUserName.text = it.userName
            }
            .flatMap {
                avatarController.getAvatarOfUser(it.id)
                    .doOnSuccess { file ->
                        Glide.with(holder.itemView)
                            .load(file)
                            .into(holder.ivAvatar)
                    }
            }.doOnError {
                it.printStackTrace()
            }.subscribe()

        holder.btnConfirm.setOnClickListener {
            rentRequestController.confirmRentRequest(rentRequest.id)
                .subscribe()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: CircleImageView
        val tvUserName: TextView
        val tvRentTime: TextView
        val btnConfirm: Button

        init {
            ivAvatar = view.findViewById(R.id.iv_avatar)
            tvUserName = view.findViewById(R.id.tv_username)
            tvRentTime = view.findViewById(R.id.tv_rent_time)
            btnConfirm = view.findViewById(R.id.btn_confirm)
        }
    }

}