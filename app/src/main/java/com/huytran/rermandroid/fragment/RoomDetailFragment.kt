package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.huytran.grpcdemo.generatedproto.RentRequest
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.ImageViewAdapter
import com.huytran.rermandroid.adapter.RentRequestAdapter
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.MessageController
import com.huytran.rermandroid.data.remote.RentRequestController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.huytran.rermandroid.utilities.UtilityFunctions
import com.opensooq.pluto.listeners.OnItemClickListener
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_room.*
import javax.inject.Inject

class RoomDetailFragment @Inject constructor(private val room: RoomData, private val isOwned: Boolean) :
    BaseFragment() {

    @Inject
    lateinit var messageController: MessageController
    @Inject
    lateinit var rentRequestController: RentRequestController
    @Inject
    lateinit var avatarController: AvatarController
    @Inject
    lateinit var userController: UserController

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
        tv_description.text = room.description
        tv_cost.text = room.price.toString()

        tv_message.visibility = if (isOwned) View.GONE else View.VISIBLE

        btn_rent.visibility = if (isOwned) View.GONE else View.VISIBLE
        llRent.visibility = if (isOwned) View.GONE else View.VISIBLE
        btn_rent.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(context!!, DatePickerFragment(room.id))
        }

        llRequest.visibility = if (isOwned) View.VISIBLE else View.GONE
        rvRequest.layoutManager = LinearLayoutManager(activity)

        rentRequestController.getRentRequestOfRoom(room.id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object : SingleObserver<List<RentRequest>> {
                override fun onSuccess(t: List<RentRequest>) {
                    rvRequest.apply {
                        adapter = RentRequestAdapter(
                            t,
                            context,
                            avatarController,
                            userController,
                            rentRequestController
                        )
                        adapter?.notifyDataSetChanged()
                    }
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        tv_message.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                context!!,
                ChatFragment(
                    room.ownerId
                )
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}