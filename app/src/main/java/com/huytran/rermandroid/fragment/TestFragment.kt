package com.huytran.rermandroid.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.remote.DataController
import com.huytran.rermandroid.fragment.base.BaseFragment
import javax.inject.Inject

class TestFragment @Inject constructor() : BaseFragment() {

//    @Inject
//    lateinit var serverChannel: ManagedChannel

    @Inject lateinit var dataController: DataController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.test_fragment, container, false)
        val btn = view.findViewById<Button>(R.id.test_grpc_btn)
        btn.setOnClickListener {
            dataController.user.signup()
        }
        return view
    }

}