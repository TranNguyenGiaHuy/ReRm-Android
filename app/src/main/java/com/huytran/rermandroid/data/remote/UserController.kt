package com.huytran.rermandroid.data.remote

import android.content.SharedPreferences
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.UserRepository
import io.grpc.Channel
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class UserController(
    private val channel: Channel,
    private val privatePreferences: SharedPreferences,
    private val userRepository: UserRepository
) {

    fun signup(username: String, password: String) {
        val stub = UserServiceGrpc.newBlockingStub(channel)
        val signUpResponse = stub.signUp(
            SignUpRequest.newBuilder()
                .setName(username)
                .setPassword(password)
                .build()
        )

        Timber.e(
            if (signUpResponse.resultCode == 0) "Sign up Success" else "Sign up Fail"
        )

        privatePreferences.edit().putString("session", signUpResponse.token).apply()
    }

    fun login(username: String, password: String) {
        val stub = UserServiceGrpc.newBlockingStub(channel)

        val loginResponse = stub.login(
            LoginRequest.newBuilder()
                .setName(username)
                .setPassword(password)
                .build()
        )

        Timber.e(
            if (loginResponse.resultCode == 0) "Login Success" else "Login Fail"
        )

        privatePreferences.edit().putString("session", loginResponse.token).apply()
    }

    fun logout() {
//        val token = UtilityFunctions.tokenHeader(privatePreferences) ?: return
        val stub = UserServiceGrpc.newBlockingStub(channel)
//        MetadataUtils.attachHeaders(
//            stub,
//            token
//        )

        val logoutResponse = stub.logout(
            LogoutRequest.newBuilder()
                .build()
        )

        privatePreferences.edit().putString("session", "").apply()
    }

    fun getUserInfo() {
//        val token = UtilityFunctions.tokenHeader(privatePreferences)
        val stub = UserServiceGrpc.newBlockingStub(channel)
//        token?.let {
//            MetadataUtils.attachHeaders(
//                stub,
//                it
//            )
//        }

        val getUserInfoResponse = stub.getInfo(
            GetInfoRequest.newBuilder()
                .build()
        )

        Timber.e(
            if (getUserInfoResponse.resultCode == 0) "Get Success" else "Get Fail"
        )

        if (getUserInfoResponse.resultCode != 0) {
            return
        }

        val user = User(
            name = getUserInfoResponse.name,
            userName = getUserInfoResponse.userName,
            avatarId = getUserInfoResponse.avatarId,
            phoneNumber = getUserInfoResponse.phoneNumber,
            idCard = getUserInfoResponse.idCard,
            tsCardDated = getUserInfoResponse.tsCardDated,
            tsDateOfBirth = getUserInfoResponse.tsDateOfBirth
        )
        userRepository.addUser(
            user
        ).observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe()

    }

}