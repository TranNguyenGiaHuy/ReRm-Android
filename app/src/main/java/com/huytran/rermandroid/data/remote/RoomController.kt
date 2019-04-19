package com.huytran.rermandroid.data.remote

import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class RoomController(
    private val channel: Channel
) {

    fun createRoom(
        title: String,
        square: Float,
        address: String,
        price: Long,
        type: Int,
        numberOfFloor: Int,
        hasFurniture: Boolean,
        maxMember: Int,
        cookingAllowance: Boolean,
        homeType: Int,
        prepaid: Long,
        description: String
    ): Completable {
        val stub = RoomServiceGrpc.newStub(channel)

        val request = CreateRoomRequest.newBuilder()
            .setRoom(
                Room.newBuilder()
                    .setTitle(title)
                    .setSquare(square)
                    .setAddress(address)
                    .setPrice(price)
                    .setType(type)
                    .setNumberOfFloor(numberOfFloor)
                    .setHasFurniture(hasFurniture)
                    .setMaxMember(maxMember)
                    .setCookingAllowance(cookingAllowance)
                    .setHomeType(homeType)
                    .setPrepaid(prepaid)
                    .setDescription(description)
                    .build()
            )
            .build()

        return Completable.create { emitter ->

            val responseObserver = object : StreamObserver<CreateRoomResponse> {
                override fun onNext(value: CreateRoomResponse?) {
                    if (value == null
                        || value.resultCode != ResultCode.RESULT_CODE_VALID
                    ) {
                        emitter.onError(
                            Throwable(
                                "Create Room Fail"
                            )
                        )
                    }
                }

                override fun onError(t: Throwable?) {
                    emitter.onError(
                        Throwable(
                            "Create Room Fail"
                        )
                    )
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            stub.createRoom(
                request,
                responseObserver
            )

        }
    }

    fun getAllRoom(): Single<List<Room>> {
        var roomList: List<Room> = arrayListOf()

        val stub = RoomServiceGrpc.newStub(channel)
        val request = GetAllRoomRequest.newBuilder().build()

        val errorThrowable = Throwable("Get Room Fail")

        return Single.create<List<Room>> { emitter ->

            val responseObserver = object : StreamObserver<GetAllRoomResponse> {
                override fun onNext(value: GetAllRoomResponse?) {
                    value?.let { it ->
                        if (it.resultCode != ResultCode.RESULT_CODE_VALID) {
                            emitter.onError(
                                errorThrowable
                            )
                            Timber.e(errorThrowable, "Error With Code: ${value.resultCode}")
                        } else {
                            roomList = value.roomList
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(errorThrowable)
                }

                override fun onCompleted() {
                    emitter.onSuccess(roomList)
                }
            }

            stub.getAllRoom(
                request,
                responseObserver
            )

        }
    }
}