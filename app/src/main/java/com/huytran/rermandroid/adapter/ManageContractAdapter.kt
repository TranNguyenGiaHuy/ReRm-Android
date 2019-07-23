package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huytran.grpcdemo.generatedproto.Contract
import com.huytran.grpcdemo.generatedproto.Room
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.huytran.rermandroid.utilities.UtilityFunctions
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.manage_contract_item.view.*

class ManageContractAdapter(
    val items: List<Contract>,
    val context: Context,
    val roomController: RoomController
) : RecyclerView.Adapter<ManageContractAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.manage_contract_item, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contract: Contract = items[position]

        holder.tvOwner.text = contract.ownerName
        holder.tvRenter.text = contract.renterName
        holder.tvStart.text = UtilityFunctions.timestampToString(contract.tsStart)
        holder.tvEnd.text = UtilityFunctions.timestampToString(contract.tsEnd)
        holder.tvRoom.text = contract.roomName

        val now = System.currentTimeMillis()
        holder.tvStatus.text = if (now < contract.tsStart) "Not Start"
        else if (contract.tsStart <= now && now <= contract.tsEnd) "Active"
        else "Terminated"

        holder.itemView.setOnClickListener {
            roomController.getRoom(contract.roomId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : SingleObserver<Room> {
                    override fun onSuccess(t: Room) {
                        TransactionManager.replaceFragmentWithWithBackStack(
                            context,
                            RoomDetailFragment(
                                RoomData(t),
                                false
                            )
                        )
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
        val tvOwner = view.tvOwner
        val tvRenter = view.tvRenter
        val tvStatus = view.tvStatus
        val tvStart = view.tvStart
        val tvEnd = view.tvEnd
        val tvRoom = view.tvRoom
//        val tvReceived = view.tvReceived
//        val tvSent = view.tvSent
//        val btnRenew = view.btnRenew
    }
}