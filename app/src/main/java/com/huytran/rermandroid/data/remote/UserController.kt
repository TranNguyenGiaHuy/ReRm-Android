package com.huytran.rermandroid.data.remote

import android.content.SharedPreferences
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.utilities.AppConstants
import io.grpc.Channel
import io.grpc.StatusRuntimeException
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class UserController(
    private val channel: Channel,
    private val privatePreferences: SharedPreferences,
    private val userRepository: UserRepository
) {

    fun signup(username: String, password: String) {
        val stub = UserServiceGrpc.newBlockingStub(channel).withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
        val signUpResponse = stub.signUp(
            SignUpRequest.newBuilder()
                .setName(username)
                .setPassword(password)
                .build()
        )

        privatePreferences.edit().putString("session", signUpResponse.token).apply()
    }

    fun login(username: String, password: String): Completable {
        val stub = UserServiceGrpc.newBlockingStub(channel).withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
        val loginResponse : LoginResponse

        try {
            loginResponse = stub.login(
                LoginRequest.newBuilder()
                    .setName(username)
                    .setPassword(password)
                    .build()
            )
        } catch (e: StatusRuntimeException) {
            return Completable.error {
                Throwable("Login Fail")
            }
        }

        return if (loginResponse.resultCode == 0
            && loginResponse.token.isNotBlank()
        ) {
            privatePreferences.edit().putString("session", loginResponse.token).apply()
            Completable.complete()
        } else {
            Completable.error {
                Throwable("Login Fail")
            }
        }

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