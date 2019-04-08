package com.huytran.rermandroid.data.remote

import android.content.SharedPreferences
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.utilities.AppConstants
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class UserController(
    private val channel: Channel,
    private val privatePreferences: SharedPreferences,
    private val userRepository: UserRepository
) {

    fun signup(username: String, password: String): Completable {
        val stub = UserServiceGrpc.newStub(channel)
            .withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)

        val signUpRequest = SignUpRequest.newBuilder()
            .setName(username)
            .setPassword(password)
            .build()


        return Completable.create {
            val signupResponseObserver = object : StreamObserver<SignUpResponse> {
                override fun onNext(value: SignUpResponse?) {
                    value?.let { signUpResponse ->
                        if (signUpResponse.resultCode == 0
                            && signUpResponse.token.isNotBlank()
                        ) {
                            privatePreferences.edit().putString("session", signUpResponse.token).apply()
                        } else {
                            it.onError(
                                Throwable("Sign Up Fail")
                            )
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    it.onError(
                        Throwable("Sign Up Fail")
                    )
                }

                override fun onCompleted() {
                    it.onComplete()
                }

            }

            stub.signUp(
                signUpRequest,
                signupResponseObserver
            )
        }
    }

    fun login(username: String, password: String): Completable {
        val stub =
            UserServiceGrpc.newStub(channel).withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)

        val loginRequest = LoginRequest.newBuilder()
            .setName(username)
            .setPassword(password)
            .build()

        return Completable.create {

            val loginResponseObserver = object : StreamObserver<LoginResponse> {
                override fun onNext(value: LoginResponse?) {
                    value?.let { loginResponse ->
                        if (loginResponse.resultCode == 0
                            && loginResponse.token.isNotBlank()
                        ) {
                            privatePreferences.edit().putString("session", loginResponse.token).apply()
                        } else {
                            it.onError(
                                Throwable("Invalid Username or Password")
                            )
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    it.onError(
                        Throwable("Login Fail")
                    )
                }

                override fun onCompleted() {
                    it.onComplete()
                }

            }

            stub.login(
                loginRequest,
                loginResponseObserver
            )
        }

    }

    fun logout() {
        val stub = UserServiceGrpc.newBlockingStub(channel)

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

    fun loginWithToken(): Completable {
        val stub = UserServiceGrpc.newStub(channel).withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)

        val loginWithTokenRequest = LoginWithTokenRequest.newBuilder().build()

        return Completable.create { emitter ->

            val loginWithTokenWithTokenResponseObserver = object : StreamObserver<LoginWithTokenResponse> {
                override fun onNext(value: LoginWithTokenResponse?) {
                    value?.let {response ->
                        if (response.resultCode == 0) {
                            emitter.onComplete()
                        } else {
                            emitter.onError(
                                Throwable(
                                    "Login Fail"
                                )
                            )
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    emitter.onError(
                        Throwable(
                            "Login Fail"
                        )
                    )
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            stub.loginWithToken(
                loginWithTokenRequest,
                loginWithTokenWithTokenResponseObserver
            )

        }
    }

}