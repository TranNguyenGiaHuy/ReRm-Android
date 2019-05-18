package com.huytran.rermandroid.data.remote

import android.content.Context
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable

class MessageController(
    private val context: Context,
    private val channel: Channel) {

    fun addToken(token: String) : Completable {
        val stub = MessageServiceGrpc.newStub(channel)
        val request = AddFirebaseTokenRequest.newBuilder()
            .setToken(token)
            .build()

        return Completable.create {emitter ->
            val responseObserver = object: StreamObserver<AddFirebaseTokenResponse> {
                override fun onNext(value: AddFirebaseTokenResponse) {
                    if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                        emitter.onError(
                            Throwable("Add Token Fail")
                        )
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        Throwable("Add Token Fail")
                    )
                }

                override fun onCompleted() {
                }

            }

            stub.addFirebaseToken(
                request,
                responseObserver
            )
        }
    }

    fun sendMessage(message: String, to: Long) : Completable {
        val stub = MessageServiceGrpc.newStub(channel)
        val request = SendMessageRequest.newBuilder()
            .setMessage(message)
            .setTo(to)
            .build()

        return Completable.create { emitter ->
            val responseObserver = object: StreamObserver<SendMessageResponse> {
                override fun onNext(value: SendMessageResponse) {
                    if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                        emitter.onError(
                            Throwable("Send Message Fail")
                        )
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        Throwable("Send Message Fail")
                    )
                }

                override fun onCompleted() {
                }

            }

            stub.sendMessage(
                request,
                responseObserver
            )
        }
    }

}