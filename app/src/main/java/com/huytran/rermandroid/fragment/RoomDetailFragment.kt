package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.ImageViewAdapter
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.utilities.UtilityFunctions
import com.opensooq.pluto.listeners.OnItemClickListener
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.detail_room.*
import kotlinx.android.synthetic.main.manage_post_item.*
import javax.inject.Inject

class RoomDetailFragment @Inject constructor(private val room: RoomData, private val isOwned: Boolean) : BaseFragment() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.detail_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        room.imageList?.let {
            if (it.isNotEmpty()) {
                val adapter = ImageViewAdapter(
                    it,
                    OnItemClickListener { _, _ -> },
                    object : ImageViewAdapter.Listener {

                        override fun doubleClickListener() {
                        }

                    }
                )

                imgViewer.create(adapter)
            }
        }

        tv_room_type.text = UtilityFunctions.longToRoomType(room.type).name
        tv_title.text = room.title
        tv_post_username.text = room.ownerName

        room.ownerAvatar?.let {
            Glide.with(img_post_profile)
                .load(it)
                .into(img_post_profile)
        }

        tv_address.text = room.address
        tv_square.text = room.square.toString()
        tv_num_of_floor.text = room.numberOfFloor.toString()
        tv_guest.text = room.maxMember.toString()
        tvCooking.text = if (room.cookingAllowance) "YES" else "NO"
        tvDescription.text = room.description
        tv_cost.text = room.price.toString()

        tv_message.visibility = if (isOwned) View.GONE else View.VISIBLE
        

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}