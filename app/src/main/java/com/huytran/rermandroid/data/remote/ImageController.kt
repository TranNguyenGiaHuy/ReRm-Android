package com.huytran.rermandroid.data.remote

import com.google.protobuf.ByteString
import com.huytran.grpcdemo.generatedproto.ImageParams
import com.huytran.grpcdemo.generatedproto.ImageServiceGrpc
import com.huytran.grpcdemo.generatedproto.UploadMultiImageRequest
import com.huytran.grpcdemo.generatedproto.UploadMultiImageResponse
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import java.io.BufferedInputStream
import java.io.File
import java.lang.Exception

class ImageController(
    private val channel: Channel
) {

    class ImageParamsForUpload(val path: String, val name: String)

    fun uploadMultiImage(
        roomId: Long,
        imageParamsList: List<ImageParamsForUpload>
    ): Completable {

        val stub = ImageServiceGrpc.newStub(channel)

        val bufferSize = 256 * 1024
        val buffer = ByteArray(bufferSize)
        val errorThrowable = Throwable("Upload Fail")

        return Completable.create { emitter ->

            val responseObserver = object : StreamObserver<UploadMultiImageResponse> {
                override fun onNext(value: UploadMultiImageResponse?) {
                    value?.let {
                        if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                            emitter.onError(errorThrowable)
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(errorThrowable)
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }

            }

            val uploadRequestObserver = stub.uploadMultiImage(responseObserver)

            try {
                imageParamsList.forEach { imageParamsForUpload ->
                    val file = File(imageParamsForUpload.path)
                    val imageStream = BufferedInputStream(
                        file.readBytes().inputStream()
                    )
                    var length = imageStream.read(buffer, 0, bufferSize)
                    while (length != -1) {
                        val currentContent = ByteString.copyFrom(buffer, 0, bufferSize)
                        length = imageStream.read(buffer, 0, bufferSize)

                        uploadRequestObserver.onNext(
                            UploadMultiImageRequest.newBuilder()
                                .setRoomId(roomId)
                                .setImage(
                                    ImageParams.newBuilder()
                                        .setName(imageParamsForUpload.name)
                                        .setContent(currentContent)
                                        .setIsLast(length == -1)
                                        .build()
                                )
                                .build()
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(errorThrowable)
            }

        }
    }

}