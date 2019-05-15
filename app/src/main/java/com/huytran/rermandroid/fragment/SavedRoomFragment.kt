package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huytran.grpcdemo.generatedproto.Room
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.adapter.PostAdapter
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.ImageController
import com.huytran.rermandroid.data.remote.SavedRoomController
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_saved_room.*
import javax.inject.Inject


class SavedRoomFragment : BaseFragment() {

    @Inject
    lateinit var avatarController: AvatarController

    @Inject
    lateinit var imageController: ImageController

    @Inject
    lateinit var savedRoomController: SavedRoomController

    private var savedRoomIdList = emptyList<Long>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_saved_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        savedRoomController.getAllSavedRoomId()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object: SingleObserver<List<Long>> {
                override fun onSuccess(t: List<Long>) {
                    savedRoomIdList = t
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        savedRoomController.getAllSavedRoom()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object : SingleObserver<List<Room>> {
                override fun onSuccess(t: List<Room>) {
                    recyclerView.apply {
                        val roomData = t.map { room ->
                            RoomData(room)
                        }.sortedByDescending {
                            it.id
                        }

                        adapter = PostAdapter(
                            ArrayList(roomData),
                            this.context,
                            avatarController,
                            imageController,
                            savedRoomController,
                            savedRoomIdList.toMutableList())
                        adapter?.notifyDataSetChanged()
                    }
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeSelectedBottomNavigationBaseOnFragment(this)
    }
}
