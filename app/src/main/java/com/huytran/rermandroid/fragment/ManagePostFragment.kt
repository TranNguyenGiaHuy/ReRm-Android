package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.ManagePostAdapter
import com.huytran.rermandroid.adapter.PostAdapter
import com.huytran.rermandroid.data.local.entity.Room
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_manage_contract.*
import javax.inject.Inject

class ManagePostFragment @Inject constructor() : BaseFragment(){
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_manage_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "Manage Post"

        val listRoom : ArrayList<Room> = ArrayList()
        val room1 : Room = Room(1, 2.0F,2)
        val room2 : Room = Room(1,1F,3)
        listRoom.add(room1)
        listRoom.add(room2)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ManagePostAdapter(listRoom,this.context)
        }
    }
}