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
import com.huytran.rermandroid.utilities.AppConstants
import com.huytran.rermandroid.utilities.UtilityFunctions
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

class PaymentRequestAdapter(
    private val context: Context,
    private val paymentList: MutableList<Payment>,
    private val avatarController: AvatarController,
    private val paymentController: PaymentController
) : RecyclerView.Adapter<PaymentRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_payment_request, parent, false)
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

        holder.tvContent.text = "Please add bill from " +
                "${UtilityFunctions.timestampToString(payment.tsStart)} " +
                "to ${UtilityFunctions.timestampToString(payment.tsEnd)}."

        when (UtilityFunctions.longToPaymentStatus(payment.status)) {
            AppConstants.PaymentStatus.WAITING_PAYMENT -> {
                holder.tvContent.text = "Your bill for this room from ${UtilityFunctions.timestampToString(payment.tsStart)} to ${UtilityFunctions.timestampToString(payment.tsEnd)}:\n" +
                        "Room bill: ${payment.amount} VND\n" +
                        "Electricity bill: ${payment.electricityBill} VND\n" +
                        "Water bill: ${payment.waterBill} VND\n" +
                        "Total : ${payment.amount + payment.electricityBill + payment.waterBill} VND\n" +
                        "Please pay bill to the owner."
                holder.btnSend.text = "Send Paid Request"
                holder.btnSend.setOnClickListener {
                    paymentController.requestPaid(payment.id)
                        .observeOn(AndroidSchedulers.mainThread())
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
            AppConstants.PaymentStatus.WAITING_CONFIRM -> {
                holder.tvContent.text = "The renter has sent paid request for bill from ${UtilityFunctions.timestampToString(payment.tsStart)} to ${UtilityFunctions.timestampToString(payment.tsEnd)}. " +
                        "If you have received the money, please confirm."
                holder.btnSend.text = "Confirm Paid Request"
                holder.btnSend.setOnClickListener {
                    paymentController.confirmPayment(payment.id)
                        .observeOn(AndroidSchedulers.mainThread())
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
            else -> {
                paymentList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: CircleImageView = view.findViewById(R.id.ivAvatar)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val btnSend: Button = view.findViewById(R.id.btn_send)
    }

}