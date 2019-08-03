package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.huytran.grpcdemo.generatedproto.Room
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.adapter.PostAdapter
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_explore.*
import javax.inject.Inject
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.*
import io.reactivex.Single
import kotlinx.android.synthetic.main.layout_search.*
import java.io.File


class ExploreFragment : BaseFragment() {

    @Inject
    lateinit var roomController: RoomController

    @Inject
    lateinit var avatarController: AvatarController

    @Inject
    lateinit var imageController: ImageController

    @Inject
    lateinit var savedRoomController: SavedRoomController

    @Inject
    lateinit var userRepository: UserRepository

    private var savedRoomIdList = emptyList<Long>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_explore, container, false)
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

        refreshData()

        btn_fab.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(context!!, CreatePostFragment())
        }

        pullToRefresh.setOnRefreshListener {
            refreshData()
            pullToRefresh.isRefreshing = false
        }

        searchLocation.setOnClickListener{
            MaterialDialog(context!!).show {
                input()
                title(text = "What's your location?")
                negativeButton(R.string.text_cancel)
            }
        }

        searchType.setOnClickListener{
            MaterialDialog(context!!).show {
                listItemsSingleChoice(R.array.roomType)
                title(text = "Choosing the type ")
                positiveButton(R.string.text_select)
                negativeButton(R.string.text_cancel)
            }
        }

        searchPrice.setOnClickListener {
            MaterialDialog(context!!).show {
                customView(R.layout.dialog_search_price)
                title(text = "Choosing price ")
                positiveButton {R.string.text_select}
                negativeButton(R.string.text_cancel)
            }
        }

        searchSquare.setOnClickListener {
            MaterialDialog(context!!).show {
                customView(R.layout.dialog_search_square)
                title(text = "Choosing square ")
                positiveButton {R.string.text_select}
                negativeButton(R.string.text_cancel)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeSelectedBottomNavigationBaseOnFragment(this)
    }

    private fun refreshData() {
        roomController.getAllRoom()
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
                            userRepository,
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
}
