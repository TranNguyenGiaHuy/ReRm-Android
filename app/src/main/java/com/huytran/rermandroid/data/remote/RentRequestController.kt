package com.huytran.rermandroid.data.remote

import android.content.Context
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class RentRequestController(
    private val context: Context,
    private val channel: Channel
) {

    fun addRentRequest(roomId: Long, from: Long, to: Long): Completable {
        val stub = RentRequestServiceGrpc.newStub(channel)
        val request = AddRentRequestRequest.newBuilder()
            .setRoomId(roomId)
            .setTsStart(from)
            .setTsEnd(to)
            .build()

        return Completable.create { emitter ->
            val throwable = Throwable("Add Rent Request Fail")
            val response = object : StreamObserver<AddRentRequestResponse> {
                override fun onNext(value: AddRentRequestResponse?) {
                    if (value?.resultCode != ResultCode.RESULT_CODE_VALID) {
                        emitter.onError(throwable)
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(throwable)
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            stub.addRentRequest(request, response)
        }
    }

    fun confirmRentRequest(rentRequestId: Long): Completable {
        val stub = RentRequestServiceGrpc.newStub(channel)
        val request = ConfirmRentRequestRequest.newBuilder()
            .setId(rentRequestId)
            .build()

        return Completable.create { emitter ->
            val throwable = Throwable("Confirm Rent Request Fail")
            val response = object : StreamObserver<ConfirmRentRequestResponse> {
                override fun onNext(value: ConfirmRentRequestResponse?) {
                    if (value?.resultCode != ResultCode.RESULT_CODE_VALID) {
                        emitter.onError(throwable)
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(throwable)
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            stub.confirmRentRequest(request, response)
        }
    }

    fun getRentRequestOfRoom(roomId: Long): Single<List<RentRequest>> {
        val stub = RentRequestServiceGrpc.newStub(channel)
        val request = GetRentRequestOfRoomRequest.newBuilder()
            .setId(roomId)
            .build()

        return Single.create<List<RentRequest>> { emitter ->
            val throwable = Throwable("Get Rent Request Fail")
            var rentRequestList = listOf<RentRequest>()
            val response = object : StreamObserver<GetRentRequestOfRoomResponse> {
                override fun onNext(value: GetRentRequestOfRoomResponse) {
                    if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                        emitter.onError(throwable)
                    }
                    rentRequestList = value.rentRequestList
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(throwable)
                }

                override fun onCompleted() {
                    emitter.onSuccess(rentRequestList)
                }
            }

            stub.getRentRequestOfRoom(request, response)
        }
    }

    fun cancelRentRequest(requestId: Long): Completable {
        val stub = RentRequestServiceGrpc.newStub(channel)
        val request = CancelRentRequestRequest.newBuilder()
            .setRequestId(requestId)
            .build()
        return Completable.create {emitter ->
            val throwable = Throwable("Cancel Rent Request Fail")
            val responseObserver = object : StreamObserver<CancelRentRequestResponse> {
                override fun onNext(value: CancelRentRequestResponse) {
                    if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                        emitter.onError(throwable)
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(throwable)
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            stub.cancelRentRequest(request, responseObserver)
        }
    }

    fun getRentRequestOfUserAndRoom(roomId : Long): Maybe<RentRequest> {
        val stub = RentRequestServiceGrpc.newStub(channel)
        val request = GetRentRequestOfRoomAndUserRequest.newBuilder()
            .setId(roomId)
            .build()

        return Maybe.create<RentRequest> {emitter ->
            val response = object: StreamObserver<GetRentRequestOfRoomAndUserResponse> {
                override fun onNext(value: GetRentRequestOfRoomAndUserResponse) {
                    value.rentRequest?.let {
                        emitter.onSuccess(
                            it
                        )
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    t?.let {
                        emitter.onError(t)
                    }
                }

                override fun onCompleted() {
                }
            }
            stub.getRentRequestOfRoomAndUser(request, response)
        }
    }

    fun update(roomId: Long, tsStart: Long, tsEnd: Long): Single<RentRequest> {
        val stub = RentRequestServiceGrpc.newStub(channel)
        val request = UpdateRentRequestRequest.newBuilder()
            .setId(roomId)
            .setTsStart(tsStart)
            .setTsEnd(tsEnd)
            .build()

        return Single.create<RentRequest> {emitter ->
            val response = object: StreamObserver<UpdateRentRequestResponse> {
                override fun onError(t: Throwable?) {
                    t?.let {
                        t.printStackTrace()
                        emitter.onError(t)
                    }
                }

                override fun onNext(value: UpdateRentRequestResponse) {
                    emitter.onSuccess(value.rentRequest)
                }

                override fun onCompleted() {
                }

            }

            stub.updateRentRequest(request, response)
        }
    }

}