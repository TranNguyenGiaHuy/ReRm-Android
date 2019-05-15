package com.huytran.rermandroid.data.remote

import android.content.Context
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.Single

class SavedRoomController(
    private val context: Context,
    private val channel: Channel) {

    fun saveRoom(roomId: Long): Completable {
        val stub = SavedRoomServiceGrpc.newStub(channel)
        val request = SaveRoomRequest.newBuilder()
            .setRoomId(roomId)
            .build()
        val throwable = Throwable("Save Room Fail")
        return Completable.create { emitter ->
            val responseObserver = object: StreamObserver<SaveRoomResponse> {
                override fun onNext(value: SaveRoomResponse) {
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

            stub.saveRoom(
                request,
                responseObserver
            )
        }
    }

    fun unsaveRoom(roomId: Long): Completable {
        val stub = SavedRoomServiceGrpc.newStub(channel)
        val request = UnsaveRoomRequest.newBuilder()
            .setRoomId(roomId)
            .build()
        val throwable = Throwable("Unsave Room Fail")
        return Completable.create { emitter ->
            val responseObserver = object: StreamObserver<UnsaveRoomResponse> {
                override fun onNext(value: UnsaveRoomResponse) {
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

            stub.unsaveRoom(
                request,
                responseObserver
            )
        }
    }

    fun getAllSavedRoomId() : Single<List<Long>> {
        val stub = SavedRoomServiceGrpc.newStub(channel)
        val request = GetAllSavedRoomIdRequest.newBuilder().build()
        val throwable = Throwable("Get All Saved Room Id Fail")

        return Single.create<List<Long>> {emitter ->
            var roomIdList = emptyList<Long>()

            val response = object: StreamObserver<GetAllSavedRoomIdResponse> {
                override fun onNext(value: GetAllSavedRoomIdResponse) {
                    if (value.resultCode != ResultCode.RESULT_CODE_VALID
                        || value.roomIdList == null){
                        emitter.onError(throwable)
                    } else {
                        roomIdList = value.roomIdList
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(throwable)
                }

                override fun onCompleted() {
                    emitter.onSuccess(roomIdList)
                }

            }

            stub.getAllSavedRoomId(
                request,
                response
            )
        }
    }

    fun getAllSavedRoom() : Single<List<Room>> {
        val stub = RoomServiceGrpc.newStub(channel)
        val request = GetAllSavedRoomRequest.newBuilder().build()
        val throwable = Throwable("Get All Saved Room Fail")

        return Single.create<List<Room>> {emitter ->
            var roomList = emptyList<Room>()
            val response = object : StreamObserver<GetAllSavedRoomResponse> {
                override fun onNext(value: GetAllSavedRoomResponse) {
                    if (value.resultCode != ResultCode.RESULT_CODE_VALID
                        ||value.roomList == null) {
                        emitter.onError(throwable)
                    } else {
                        roomList = value.roomList
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(throwable)
                }

                override fun onCompleted() {
                    emitter.onSuccess(roomList)
                }

            }

            stub.getAllSavedRoom(request, response)
        }
    }

}