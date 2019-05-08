package com.huytran.rermandroid.data.remote

import android.content.Context
import com.google.protobuf.ByteString
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.utilities.AppConstants
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class ImageController(
    private val context: Context,
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

    private fun downloadFile(id: Long): Single<File> {
        return Single.create<ByteArray> { emitter ->
            val stub = ImageServiceGrpc.newStub(channel)
            val result = ByteArrayOutputStream()

            val request = DownloadRequest.newBuilder()
                .setId(id)
                .build()
            val response = object : StreamObserver<DownloadResponse> {
                override fun onNext(value: DownloadResponse?) {
                    value?.let { downloadResponse ->
                        if (downloadResponse.resultCode == ResultCode.RESULT_CODE_VALID) {
                            result.write(downloadResponse.image.toByteArray())
                        } else {
                            emitter.onError(
                                Throwable(
                                    "Download File Fail"
                                )
                            )
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        Throwable(
                            "Download File Fail"
                        )
                    )
                }

                override fun onCompleted() {
                    emitter.onSuccess(result.toByteArray())
                }

            }

            stub.downloadFile(
                request,
                response
            )
        }.flatMapCompletable {
            saveFileToCache(it, id)
        }.toSingle {
            File(context.cacheDir, AppConstants.KEY_IMAGE_WITH_ID + id.toString())
        }
    }

    private fun saveFileToCache(byteArray: ByteArray, id: Long): Completable {
        if (byteArray.isEmpty()) {
            return Completable.error(
                Throwable(
                    "File Empty"
                )
            )
        }

        return try {
            val newFile = File(context.cacheDir, AppConstants.KEY_IMAGE_WITH_ID + id.toString())
            val fileOutputStream = FileOutputStream(newFile, false)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()

            Completable.complete()
        } catch (e: Exception) {
            Completable.error(
                Throwable(
                    "Save Fail"
                )
            )
        }
    }

    fun getAllImageOfRoom(roomId: Long): Single<List<File>> {
        val stub = ImageServiceGrpc.newStub(channel)

        val existedFileMap = mutableListOf<Pair<Long, File>>()

        return Single.create<List<Long>> { emitter ->
            var idList = emptyList<Long>()

            val request = GetFileOfRoomRequest.newBuilder()
                .setRoomId(roomId)
                .build()

            val response = object : StreamObserver<GetFileOfRoomResponse> {
                override fun onNext(value: GetFileOfRoomResponse?) {
                    if (value == null
                        || value.resultCode != ResultCode.RESULT_CODE_VALID
                    ) {
                        emitter.onError(
                            Throwable(
                                "Get File Id Fail"
                            )
                        )
                    } else {
                        idList = value.fileIdList
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        Throwable(
                            "Get File Id Fail"
                        )
                    )
                }

                override fun onCompleted() {
                    emitter.onSuccess(idList)
                }
            }

            stub.getFileOfRoom(request, response)
        }.flatMap { idList ->
            val notDownloadIdList = mutableListOf<Long>()
            idList.forEach { id ->
                val file = File(context.cacheDir, AppConstants.KEY_IMAGE_WITH_ID + id.toString())
                if (file.exists()) {
                    existedFileMap.add(
                        Pair(id, file)
                    )
                } else {
                    notDownloadIdList.add(id)
                }
            }
            Single.just(notDownloadIdList.toList())
        }.flatMap { idList ->
            var currentId = 0L
            Flowable.range(0, idList.size)
                .concatMapEager { index ->
                    currentId = idList[index]

                    downloadFile(currentId)
                        .subscribeOn(Schedulers.io())
                        .toFlowable()
                }.map {
                    Pair(currentId, it)
                }.toList()
        }.flatMap {
            existedFileMap.addAll(it)
            existedFileMap.sortBy { pair ->
                pair.first
            }
            Single.just(
                existedFileMap.map { pair ->
                    pair.second
                }
            )
        }
    }

    fun uploadFile(roomId: Long, imageParamsForUpload: ImageParamsForUpload): Completable {
        val stub = ImageServiceGrpc.newStub(channel)

        val bufferSize = 256 * 1024
        val buffer = ByteArray(bufferSize)
        val errorThrowable = Throwable("Upload Fail")

        return Completable.create { emitter ->
            val responseObserver = object : StreamObserver<UploadFileResponse> {
                override fun onNext(value: UploadFileResponse?) {
                    value?.let {
                        if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                            emitter.onError(errorThrowable)
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        errorThrowable
                    )
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }
            }
            val requestObserver = stub.uploadFile(responseObserver)

            try {
                val file = File(imageParamsForUpload.path)
                val imageStream = BufferedInputStream(
                    file.readBytes().inputStream()
                )
                var length = imageStream.read(buffer, 0, bufferSize)
                while (length != -1) {
                    requestObserver.onNext(
                        UploadFileRequest.newBuilder()
                            .setRoomId(roomId)
                            .setName(imageParamsForUpload.name)
                            .setContent(ByteString.copyFrom(buffer, 0, bufferSize))
                            .build()
                    )
                    length = imageStream.read(buffer, 0, bufferSize)
                }
                requestObserver.onCompleted()
            } catch (e: Exception) {
                e.printStackTrace()
                emitter.onError(errorThrowable)
            }
            emitter.onComplete()
        }
    }

}