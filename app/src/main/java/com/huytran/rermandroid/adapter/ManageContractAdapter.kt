package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.entity.Contract
import com.huytran.rermandroid.data.local.entity.Room
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import kotlinx.android.synthetic.main.detail_room.view.*
import kotlinx.android.synthetic.main.manage_contract_item.view.*

class ManageContractAdapter(val items : ArrayList<Contract>, val context: Context) : RecyclerView.Adapter<ManageContractAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.manage_contract_item, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contract : Contract = items[position]


        holder.itemView.setOnClickListener{

        }

        holder.btnRenew.setOnClickListener {

        }
    }
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvOwner = view.tvOwner
        val tvRenter = view.tvRenter
        val tvStatus = view.tvStatus
        val tvStart = view.tvStart
        val tvEnd = view.tvEnd
        val tvRoom = view.tvRoom
        val tvReceived = view.tvReceived
        val tvSent = view.tvSent
        val btnRenew = view.btnRenew
    }
}