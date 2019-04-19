package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huytran.grpcdemo.generatedproto.Room
import com.huytran.rermandroid.R
import com.huytran.rermandroid.fragment.RoomDetailFragment
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
        holder.tvPrice.text = room.price.toString()
        holder.tvAddress.text = room.address.toString()
        holder.tvDescription.text = room.description.toString()

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

        init {
            tvRoomType = view.findViewById(R.id.tv_room_type)
            tvPrice = view.findViewById(R.id.tv_post_price)
            tvAddress = view.findViewById(R.id.tv_post_location)
            tvDescription = view.findViewById(R.id.tv_post_description)
        }
    }
}

