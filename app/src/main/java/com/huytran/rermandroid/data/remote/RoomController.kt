package com.huytran.rermandroid.data.remote

import com.esafirm.imagepicker.model.Image
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

class RoomController(
    private val channel: Channel,
    private val imageController: ImageController
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
        description: String,
        imageList: List<Image>
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

        return Single.create<Long> { emitter ->

            val responseObserver = object : StreamObserver<CreateRoomResponse> {

                var roomId = -1L

                override fun onNext(value: CreateRoomResponse?) {
                    if (value == null
                        || value.resultCode != ResultCode.RESULT_CODE_VALID
                    ) {
                        emitter.onError(
                            Throwable(
                                "Create Room Fail"
                            )
                        )
                    } else {
                        roomId = value.roomId
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
                    emitter.onSuccess(roomId)
                }

            }

            stub.createRoom(
                request,
                responseObserver
            )

        }.flatMapCompletable {roomId ->
            Completable.concat {
                imageList.forEach {image ->
                    imageController.uploadFile(
                        roomId,
                        ImageController.ImageParamsForUpload(
                            image.path,
                            image.name
                        )
                    ).subscribe(object: CompletableObserver {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                    })
                }
            }
            Completable.complete()
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