package com.huytran.rermandroid.data.remote

import android.content.SharedPreferences
import com.huytran.grpcdemo.generatedproto.SignUpRequest
import com.huytran.grpcdemo.generatedproto.UserServiceGrpc
import io.grpc.ManagedChannel
import timber.log.Timber

class DataController(val serverChannel: ManagedChannel, val privatePreferences: SharedPreferences) {

    val user = User()

    inner class User {

        fun signup() {
            val stub = UserServiceGrpc.newBlockingStub(serverChannel)
            val signUpResponse = stub.signUp(
                SignUpRequest.newBuilder()
                    .setName("HuyTran")
                    .setPassword("123")
                    .build()
            )

            privatePreferences.edit().putString("session", signUpResponse.token).apply()
            Timber.e(signUpResponse.token)


            serverChannel.shutdown()
        }
    }

}