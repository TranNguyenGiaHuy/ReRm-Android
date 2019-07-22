package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.Payment
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.PaymentController
import com.huytran.rermandroid.utilities.UtilityFunctions
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class PaymentBillAdapter(
    private val context: Context,
    private val paymentList: MutableList<Payment>,
    private val avatarController: AvatarController,
    private val paymentController: PaymentController
) : RecyclerView.Adapter<PaymentBillAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_payment_add_bill, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment = paymentList[position]
        avatarController.getAvatarOfUser(payment.payerId)
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

        holder.tvTitle.text = "Please add bill from " +
                "${UtilityFunctions.timestampToString(payment.tsStart)} " +
                "to ${UtilityFunctions.timestampToString(payment.tsEnd)}."

        holder.tvRoomPrice.text = "Room price: ${payment.amount}"

        holder.btnSend.setOnClickListener {
            paymentController.addBill(
                payment.id,
                holder.etElectricityBill.text.toString().toLong(),
                holder.etWaterBill.text.toString().toLong()
            ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object: SingleObserver<Payment> {
                    override fun onSuccess(t: Payment) {
                        paymentList.removeAt(position)
                        notifyItemRemoved(position)
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                    }

                })
        }

    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: CircleImageView = view.findViewById(R.id.ivAvatar)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvRoomPrice: TextView = view.findViewById(R.id.tvRoomPrice)
        val etElectricityBill: EditText = view.findViewById(R.id.etElectricityBill)
        val etWaterBill: EditText = view.findViewById(R.id.etWaterBill)
        val btnSend: Button = view.findViewById(R.id.btn_send)
    }

}