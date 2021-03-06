package com.huytran.rermandroid.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.fragment.EditPostFragment
import com.huytran.rermandroid.fragment.RoomDetailFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.huytran.rermandroid.utilities.UtilityFunctions
import kotlinx.android.synthetic.main.manage_post_item.view.*


class ManagePostAdapter(private val items : MutableList<RoomData>, val context: Context) : RecyclerView.Adapter<ManagePostAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.manage_post_item, parent, false))
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room : RoomData = items[position]
        holder.tvPrice.text = "${room.price} VND"
        holder.tvRoomType.text = UtilityFunctions.longToRoomType(room.type).name
        holder.tvLocation.text = room.address
        holder.tvStatus.visibility = if (room.isRenting) View.VISIBLE else View.GONE
        holder.btnDelete.visibility = if (room.isRenting) View.GONE else View.VISIBLE
        holder.btnEdit.visibility = if (room.isRenting) View.GONE else View.VISIBLE
        holder.tvTitle.text = "${room.title}"
        holder.tvDescription.text = "${room.description}"

        holder.itemView.setOnClickListener{
            TransactionManager.replaceFragmentWithWithBackStack(
                context,
                RoomDetailFragment(room, true)
            )
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
        val tvTitle = view.tvTitle
        val tvLocation = view.tvLocation
        val ratingBar = view.rbManagePost
        val btnDelete = view.ivDelete
        val btnEdit = view.ivEdit
        val tvStatus = view.tvStatus
        val tvDescription = view.tvDescription

    }
}