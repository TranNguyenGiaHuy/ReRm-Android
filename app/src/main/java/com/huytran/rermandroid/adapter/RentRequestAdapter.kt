package com.huytran.rermandroid.adapter

import android.content.Context
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.RentRequest
import com.huytran.grpcdemo.generatedproto.User
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.RentRequestController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.DatePickerFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.huytran.rermandroid.utilities.UtilityFunctions
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class RentRequestAdapter(
    private val rentRequestDataList: MutableList<RentRequest>,
    private val isOwner: Boolean,
    private val context: Context,
    private val avatarController: AvatarController,
    private val userController: UserController,
    private val rentRequestController: RentRequestController
) :
    RecyclerView.Adapter<RentRequestAdapter.ViewHolder>() {

    private val avatarMap = mutableMapOf<Int, File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_rent_request, parent, false))
    }

    override fun getItemCount() = rentRequestDataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rentRequest = rentRequestDataList[position]

        val rentTime =
            "From ${UtilityFunctions.timestampToString(rentRequest.tsStart)} to ${UtilityFunctions.timestampToString(
                rentRequest.tsStart
            )}"
        holder.tvRentTime.text = rentTime
        userController.getInfoOfUser(rentRequest.renter)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object: SingleObserver<User> {
                override fun onSuccess(t: User) {
                    holder.tvUserName.text = t.userName
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        avatarController.getAvatarOfUser(rentRequest.renter)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : SingleObserver<File> {
                override fun onSuccess(t: File) {
                    Glide.with(holder.itemView)
                        .load(t)
                        .into(holder.ivAvatar)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        // btn confirm and btn edit
        holder.btnConfirm.visibility = if (isOwner) View.VISIBLE else View.GONE
        holder.btnEdit.visibility = if (isOwner) View.GONE else View.VISIBLE

        holder.btnConfirm.setOnClickListener {
            rentRequestController.confirmRentRequest(rentRequest.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object: CompletableObserver {
                    override fun onComplete() {
                        rentRequestDataList.removeAt(position)
                        notifyItemRemoved(position)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }

                })
        }

        holder.btnEdit.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                context,
                DatePickerFragment(rentRequest.roomId, true)
            )
        }

        // cancel rent request
        holder.btnCancel.setOnClickListener {
            rentRequestController.cancelRentRequest(rentRequest.id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        rentRequestDataList.removeAt(position)
                        notifyItemRemoved(position)
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
        val ivAvatar: CircleImageView
        val tvUserName: TextView
        val tvRentTime: TextView
        val btnConfirm: Button
        val btnCancel: ImageButton
        val btnEdit: Button

        init {
            ivAvatar = view.findViewById(R.id.iv_avatar)
            tvUserName = view.findViewById(R.id.tv_username)
            tvRentTime = view.findViewById(R.id.tv_rent_time)
            btnConfirm = view.findViewById(R.id.btn_confirm)
            btnCancel = view.findViewById(R.id.btn_cancel)
            btnEdit = view.findViewById(R.id.btn_edit)
        }
    }

}