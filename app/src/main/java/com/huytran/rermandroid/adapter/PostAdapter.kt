package com.huytran.rermandroid.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.entity.Room
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.fragment.ProfileFragment
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.fragment.TestFragment
import com.huytran.rermandroid.manager.TransactionManager
import kotlinx.android.synthetic.main.detail_room.view.*

class PostAdapter(val items : ArrayList<Room>, val context: Context) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room : Room = items[position]
        holder?.tvPrice?.text = room.price.toString()
        holder?.tvSquare?.text = room.square.toString()

        holder.itemView.setOnClickListener{
            TransactionManager.replaceFragmentWithWithBackStack(
                context,
                RoomDetailFragment()
            )
        }
    }
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomType = view.tv_room_type
        val tvPrice = view.tv_cost
        val tvSquare = view.tv_square
    }
}

