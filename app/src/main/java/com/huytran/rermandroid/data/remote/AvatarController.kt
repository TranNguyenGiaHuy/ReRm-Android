package com.huytran.rermandroid.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.google.protobuf.ByteString
import com.huytran.grpcdemo.generatedproto.*
import com.huytran.rermandroid.data.local.entity.Avatar
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.utilities.AppConstants
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.io.*

class AvatarController(
    private val context: Context,
    private val channel: Channel,
    private val privatePreferences: SharedPreferences,
    private val userRepository: UserRepository,
    private val avatarRepository: AvatarRepository
) {

    fun uploadAvatar(path: String, name: String): Completable {

        val stub = AvatarServiceGrpc.newStub(channel)

        val file = File(path)
        val imageStream = BufferedInputStream(
            file.readBytes().inputStream()
        )
        val bufferSize = 256 * 1024
        val buffer = ByteArray(bufferSize)

        return userRepository.getLastMaybe()
            .flatMapSingle { user ->
                Single.create<UploadAvatarResponse> { emitter ->

                    val responseObserver = object : StreamObserver<UploadAvatarResponse> {
                        var value: UploadAvatarResponse? = null

                        override fun onNext(value: UploadAvatarResponse?) {
                            value?.let {
                                if (value.resultCode != 0) {
                                    emitter.onError(
                                        Throwable("Upload Fail")
                                    )
                                }

                                this.value = value
                            }
                        }

                        override fun onError(t: Throwable?) {
                            t?.printStackTrace()
                            emitter.onError(
                                Throwable("Upload Fail")
                            )
                        }

                        override fun onCompleted() {
                            value?.let {
                                emitter.onSuccess(it)
                            }
                        }

                    }

                    val uploadAvatarRequestObserver = stub.uploadAvatar(responseObserver)

                    try {
                        var length = imageStream.read(buffer, 0, bufferSize)
                        while (length != -1) {
                            uploadAvatarRequestObserver.onNext(
                                UploadAvatarRequest.newBuilder()
                                    .setUserId(user.svId)
                                    .setName(name)
                                    .setContent(ByteString.copyFrom(buffer, 0, bufferSize))
                                    .build()
                            )
                            length = imageStream.read(buffer, 0, bufferSize)
                            Timber.e("debug onNext")
                        }
//                        uploadAvatarRequestObserver.onCompleted()
                        Timber.e("debug onComplete")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emitter.onError(
                            Throwable("Upload Fail")
                        )
                        uploadAvatarRequestObserver.onError(
                            Throwable("Upload Fail")
                        )
                    }
                    uploadAvatarRequestObserver.onCompleted()
                }
            }.flatMapCompletable {
                saveAvatarCompletable(file.readBytes())
            }

    }

    fun downloadAvatar(): Completable {
        val stub = AvatarServiceGrpc.newStub(channel)

        val request = DownloadAvatarRequest.newBuilder().build()
        val throwable = Throwable("Download Avatar Fail")

        return Single.create<ByteArray> { emitter ->

            val result = ByteArrayOutputStream()

            val responseStreamObserver = object : StreamObserver<DownloadAvatarResponse> {
                override fun onNext(value: DownloadAvatarResponse?) {
                    value?.let {
                        result.write(value.image.toByteArray())
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        throwable
                    )
                }

                override fun onCompleted() {
                    if (result.toByteArray().isEmpty()) {
                        emitter.onError(
                            throwable
                        )
                    }
                    emitter.onSuccess(result.toByteArray())
                }

            }

            stub.downloadAvatar(
                request,
                responseStreamObserver
            )

        }.flatMapCompletable {
            saveAvatarCompletable(it)
        }
    }

    fun getAvatar(): Single<File> {
        return Single.create<File> { emitter ->
            val file = File(context.cacheDir, AppConstants.KEY_AVATAR)
            if (file.exists()) {
                emitter.onSuccess(file)
            } else {
                emitter.onError(
                    Throwable("Not Have Avatar")
                )
            }
        }
    }

    private fun saveAvatarCompletable(file: ByteArray): Completable {
        if (file.isEmpty()) {
            return Completable.error(
                Throwable("Not Have File")
            )
        }
        return try {
            val newFile = File(context.cacheDir, AppConstants.KEY_AVATAR)
            val fileOutputStream = FileOutputStream(newFile, false)
//            newFile.writeBytes(file)
            fileOutputStream.write(file)
            fileOutputStream.flush()
            fileOutputStream.close()

//            val outputStream = context.openFileOutput(AppConstants.KEY_AVATAR, Context.MODE_PRIVATE)
//
//            outputStream.write(file)
//            outputStream.flush()
//            outputStream.close()

            Completable.complete()
        } catch (e: Exception) {
            e.printStackTrace()
            Completable.error(
                Throwable("Save Fail")
            )
        }
    }

    fun getAvatarOfUser(id: Long): Single<File> {
        val stub = AvatarServiceGrpc.newStub(channel)

        return Single.create<Long> { emitter ->
            val request = GetAvatarInfoOfUserRequest.newBuilder().setUserId(id).build()
            var avatarId = -1L

            val response = object : StreamObserver<GetAvatarInfoOfUserResponse> {
                override fun onNext(value: GetAvatarInfoOfUserResponse?) {
                    if (value != null) {
                        avatarId = value.avatarId
                    } else {
                        emitter.onError(
                            Throwable(
                                "Get Info Fail"
                            )
                        )
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
                    emitter.onSuccess(avatarId)
                }

            }

            stub.getAvatarInfoOfUser(
                request,
                response
            )
        }.flatMap { avatarId ->
            Single.create<File> { emitter ->
                val onDeviceFile = File(context.cacheDir, AppConstants.KEY_AVATAR_OF_USER + avatarId.toString())
                if (onDeviceFile.exists()) {
                    emitter.onSuccess(onDeviceFile)
                } else {
                    val byteArrayOutputStream = ByteArrayOutputStream()

                    val request = DownloadAvatarWithIdRequest.newBuilder().setId(avatarId).build()
                    val response = object : StreamObserver<DownloadAvatarWithIdResponse> {
                        override fun onNext(value: DownloadAvatarWithIdResponse?) {
                            if (value == null
                                || value.resultCode != ResultCode.RESULT_CODE_VALID
                            ) {
                                emitter.onError(
                                    Throwable(
                                        "Download Fail"
                                    )
                                )
                            }
                            byteArrayOutputStream.write(value?.image?.toByteArray())
                        }

                        override fun onError(t: Throwable?) {
                            t?.printStackTrace()
                            emitter.onError(
                                Throwable(
                                    "Download Fail"
                                )
                            )
                        }

                        override fun onCompleted() {
                            val file = File(context.cacheDir, AppConstants.KEY_AVATAR_OF_USER + avatarId.toString())
//                            val fileWriter = FileWriter(file.absoluteFile)
//                            val bufferedWriter = BufferedWriter(fileWriter)
//                            bufferedWriter.write(byteArrayOutputStream.toString())
                            file.writeBytes(byteArrayOutputStream.toByteArray())
                            byteArrayOutputStream.close()
//                            bufferedWriter.close()

                            emitter.onSuccess(file)
                        }
                    }

                    stub.downloadAvatarWithId(
                        request,
                        response
                    )
                }
            }
        }
    }

}