package com.huytran.rermandroid.adapter

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.entity.Room
import com.huytran.rermandroid.fragment.EditPostFragment
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import kotlinx.android.synthetic.main.detail_room.view.*
import kotlinx.android.synthetic.main.manage_post_item.view.*




class ManagePostAdapter(val items : ArrayList<Room>, val context: Context) : RecyclerView.Adapter<ManagePostAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.manage_post_item, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room : Room = items[position]
        holder.tvPrice?.text = room.price.toString()


        holder.itemView.setOnClickListener{
//            TransactionManager.replaceFragmentWithWithBackStack(
//                context,
//                RoomDetailFragment(room)
//            )
        }

        holder.btnDelete.setOnClickListener {

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        items.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position,items.size)
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.cancel()
                    }
                }
            }

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }

        holder.btnEdit.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                context,
                EditPostFragment()
            )
        }

    }
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomType = view.tvRoomType
        val tvPrice = view.tvPrice
        val tvLocation = view.tvLocation
        val ratingBar = view.rbManagePost
        val btnDelete = view.ivDelete
        val btnEdit = view.ivEdit

    }
}