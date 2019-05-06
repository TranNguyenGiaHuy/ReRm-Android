package com.huytran.rermandroid.data.remote

import android.content.SharedPreferences
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.utilities.AppConstants
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.*
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
                    t?.printStackTrace()
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
                    t?.printStackTrace()
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

    fun logout() : Completable {
        val stub = UserServiceGrpc.newStub(channel)

        val logoutRequest = LogoutRequest.newBuilder().build()

        return Completable.create { emitter ->

            val logoutResponseObserver = object : StreamObserver<LogoutResponse> {
                override fun onNext(value: LogoutResponse?) {
                    value
                }

                override fun onError(t: Throwable?) {
                    privatePreferences.edit().putString("session", "").apply()
                    emitter.onComplete()
                }

                override fun onCompleted() {
                    privatePreferences.edit().putString("session", "").apply()
                    emitter.onComplete()
                }

            }

            stub.logout(
                logoutRequest,
                logoutResponseObserver
            )
        }
    }

    fun getUserInfo(): Completable {
        val stub = UserServiceGrpc.newStub(channel)
            .withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)

        val getInfoRequest = GetInfoRequest.newBuilder().build()

        val userSingle = Single.create<User> { emitter ->

            val getInfoResponseObserver = object : StreamObserver<GetInfoResponse> {

                override fun onNext(value: GetInfoResponse?) {

                    value?.let { getUserInfoResponse ->

                        if (getUserInfoResponse.resultCode != 0) {
                            emitter.onError(
                                Throwable(
                                    "Get Info Fail"
                                )
                            )
                        }

                        val user = User(
                            svId = getUserInfoResponse.id,
                            name = getUserInfoResponse.name,
                            userName = getUserInfoResponse.userName,
                            avatarId = getUserInfoResponse.avatarId,
                            phoneNumber = getUserInfoResponse.phoneNumber,
                            idCard = getUserInfoResponse.idCard,
                            tsCardDated = getUserInfoResponse.tsCardDated,
                            tsDateOfBirth = getUserInfoResponse.tsDateOfBirth,
                            placeOfPermanent = getUserInfoResponse.placeOfPermanent
                        )

                        emitter.onSuccess(user)
                    }

                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        Throwable(
                            "Get Info Fail"
                        )
                    )
                }

                override fun onCompleted() {
                }

            }

            stub.getInfo(
                getInfoRequest,
                getInfoResponseObserver
            )

        }

        return userSingle.flatMapCompletable { user ->
            userRepository.addUser(user)
        }

    }

    fun loginWithToken(): Completable {
        val stub =
            UserServiceGrpc.newStub(channel).withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)

        val loginWithTokenRequest = LoginWithTokenRequest.newBuilder().build()

        return Completable.create { emitter ->

            val loginWithTokenWithTokenResponseObserver = object : StreamObserver<LoginWithTokenResponse> {
                override fun onNext(value: LoginWithTokenResponse?) {
                    value?.let { response ->
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
                    t?.printStackTrace()
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

    fun updateInfo(user: User): Completable {
        val stub =
            UserServiceGrpc.newStub(channel).withDeadlineAfter(AppConstants.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)

        val updateInfoRequest = UpdateUserInfoRequest.newBuilder()
            .setName(user.name)
            .setUserName(user.userName)
            .setPhoneNumber(user.phoneNumber)
            .setIdCard(user.idCard)
            .setTsCardDated(user.tsCardDated)
            .setTsDateOfBirth(user.tsDateOfBirth)
            .setPlaceOfPermanent(user.placeOfPermanent)
            .build()

        return Single.create<User> { emitter ->

            val updateInfoResponse = object : StreamObserver<UpdateUserInfoResponse> {
                override fun onNext(value: UpdateUserInfoResponse?) {
                    if (value != null) {
                        val newUser = User(
                            id = user.id,
                            svId = user.svId,
                            avatarId = user.avatarId,
                            name = value.name,
                            userName = value.userName,
                            idCard = value.idCard,
                            phoneNumber = value.phoneNumber,
                            placeOfPermanent = value.placeOfPermanent,
                            tsDateOfBirth = value.tsDateOfBirth,
                            tsCardDated = value.tsCardDated
                        )
                        emitter.onSuccess(newUser)
                    } else {
                        emitter.onError(
                            Throwable("Update Info Fail")
                        )
                    }

                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        Throwable("Update Info Fail")
                    )
                }

                override fun onCompleted() {
                }

            }

            stub.updateUserInfo(
                updateInfoRequest,
                updateInfoResponse
            )

        }.flatMapCompletable {
            userRepository.addUser(it)
        }
    }

}