package com.huytran.rermandroid.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.PostAdapter
import com.huytran.rermandroid.data.local.entity.Room
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.detail_room.*
import kotlinx.android.synthetic.main.fragment_explore.*
import javax.inject.Inject


class ExploreFragment @Inject constructor() : BaseFragment() {

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listRoom : ArrayList<Room> = ArrayList()
        val room1 : Room = Room(1, 2.0F,3)
        val room2 : Room = Room(1,1F,3)
        listRoom.add(room1)
        listRoom.add(room2)
        /*recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = PostAdapter(room,this.requireContext())*/
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = PostAdapter(listRoom,this.context)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
