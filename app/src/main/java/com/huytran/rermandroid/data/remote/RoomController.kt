package com.huytran.rermandroid.data.remote

import com.esafirm.imagepicker.model.Image
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.data.local.localbean.RoomData
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
        imageList: List<Image>,
        term: String,
        electricityPrice: Long,
        waterPrice: Long
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
                    .setTerm(term)
                    .setElectricityPrice(electricityPrice)
                    .setWaterPrice(waterPrice)
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

        }.flatMapCompletable { roomId ->
            Completable.concat {
                imageList.forEach { image ->
                    imageController.uploadFile(
                        roomId,
                        ImageController.ImageParamsForUpload(
                            image.path,
                            image.name
                        )
                    ).subscribe(object : CompletableObserver {
                        override fun onComplete() {
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                    })
                }
            }.andThen {
                Completable.complete()
            }
        }
    }

    fun getAllRoom(): Single<List<Room>> {
        var roomList: List<Room> = listOf()

        val stub = RoomServiceGrpc.newStub(channel)
        val request = GetAllRoomRequest.newBuilder().build()

        val errorThrowable = Throwable("Get Rooms Fail")

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

    fun getRoom(roomId: Long): Single<Room> {
        val stub = RoomServiceGrpc.newStub(channel)
        val request = GetRoomRequest.newBuilder().setRoomId(roomId).build()
        val errorThrowable = Throwable("Get Room Fail")

        return Single.create<Room> {emitter ->
            val responseObserver = object: StreamObserver<GetRoomResponse> {
                override fun onNext(value: GetRoomResponse) {
                    emitter.onSuccess(
                        value.room
                    )
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(errorThrowable)
                }

                override fun onCompleted() {
                }

            }
            stub.getRoom(request, responseObserver)
        }

    }

    fun updateRoom(
        id: Long,
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
        imageList: List<Image>,
        term: String,
        electricityPrice: Long,
        waterPrice: Long
    ): Completable {
        val stub = RoomServiceGrpc.newStub(channel)

        val request = UpdateRoomRequest.newBuilder()
            .setRoom(
                Room.newBuilder()
                    .setId(id)
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
                    .setTerm(term)
                    .setElectricityPrice(electricityPrice)
                    .setWaterPrice(waterPrice)
                    .build()
            )
            .build()

        return Single.create<Long> { emitter ->

            val responseObserver = object : StreamObserver<UpdateRoomResponse> {

                var roomId = -1L

                override fun onNext(value: UpdateRoomResponse) {
                    roomId = value.room.id
                }

                override fun onError(t: Throwable?) {
                    emitter.onError(
                        Throwable(
                            "Update Room Fail"
                        )
                    )
                }

                override fun onCompleted() {
                    emitter.onSuccess(roomId)
                }

            }

            stub.updateRoom(
                request,
                responseObserver
            )

        }.flatMapCompletable { roomId ->
            if (imageList.isEmpty()) {
                Completable.complete()
            } else {
                Completable.concat {
                    imageList.forEach { image ->
                        imageController.uploadFile(
                            roomId,
                            ImageController.ImageParamsForUpload(
                                image.path,
                                image.name
                            )
                        ).subscribe(object : CompletableObserver {
                            override fun onComplete() {

                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }

                        })
                    }
                }.andThen {
                    Completable.complete()
                }
            }
        }
    }

    fun deleteRoom(id: Long): Completable {
        val stub = RoomServiceGrpc.newStub(channel)
        val deleteRoomRequest = DeleteRoomRequest.newBuilder()
            .setId(id)
            .build()

        return Completable.create {emitter ->
            val response = object: StreamObserver<DeleteRoomResponse> {
                override fun onNext(value: DeleteRoomResponse) {
                }

                override fun onError(t: Throwable?) {
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            stub.deleteRoom(deleteRoomRequest, response)
        }
    }

    fun getAllOfUser(): Single<List<Room>> {
        val stub = RoomServiceGrpc.newStub(channel)
        val request = GetAllRoomOfUserRequest.newBuilder().build()

        return Single.create<List<Room>> {emitter ->
            val response = object: StreamObserver<GetAllRoomOfUserResponse> {
                override fun onNext(value: GetAllRoomOfUserResponse) {
                    emitter.onSuccess(value.roomList)
                }

                override fun onError(t: Throwable?) {
                    t?.let {
                        t.printStackTrace()
                        emitter.onError(t)
                    }
                }

                override fun onCompleted() {
                }

            }

            stub.getAllRoomOfUser(request, response)
        }
    }
}