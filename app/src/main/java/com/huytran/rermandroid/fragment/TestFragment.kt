package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.huytran.grpcdemo.generatedproto.SignUpRequest
import com.huytran.grpcdemo.generatedproto.UserServiceGrpc
import com.huytran.rermandroid.R
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.grpc.ManagedChannel
import javax.inject.Inject

class TestFragment @Inject constructor() : BaseFragment() {

    @Inject
    lateinit var serverChannel: ManagedChannel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.test_fragment, container, false)
        val btn = view.findViewById<Button>(R.id.test_grpc_btn)
        btn.setOnClickListener {
//            val channel = ManagedChannelBuilder
//                .forAddress("10.0.2.2", 6565)
//                .usePlaintext()
//                .build()
            val stub = UserServiceGrpc.newBlockingStub(serverChannel)
            val signUpResponse = stub.signUp(
                SignUpRequest.newBuilder()
                    .setName("HuyTran")
                    .setPassword("123")
                    .build()
            )
            Toast.makeText(
                this.activity,
                signUpResponse.token,
                Toast.LENGTH_LONG
            ).show()
            serverChannel.shutdown()
        }
        return view
    }

}