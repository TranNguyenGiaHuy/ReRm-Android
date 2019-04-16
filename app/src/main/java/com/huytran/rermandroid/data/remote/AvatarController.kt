package com.huytran.rermandroid.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.google.protobuf.ByteString
import com.huytran.grpcdemo.generatedproto.AvatarServiceGrpc
import com.huytran.grpcdemo.generatedproto.UploadAvatarRequest
import com.huytran.grpcdemo.generatedproto.UploadAvatarResponse
import com.huytran.rermandroid.data.local.entity.Avatar
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File

class AvatarController(
    private val context: Context,
    private val channel: Channel,
    private val privatePreferences: SharedPreferences,
    private val userRepository: UserRepository,
    private val avatarRepository: AvatarRepository
) {

    fun uploadAvatar(path: String, name: String) : Completable {

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
            }.flatMapCompletable { uploadAvatarResponse ->
                try {
                    val outputStream = context.openFileOutput(uploadAvatarResponse.fileName, Context.MODE_PRIVATE)

                    outputStream.write(file.readBytes())
                    outputStream.flush()
                    outputStream.close()

                    avatarRepository.insert(
                        Avatar(
                            fileName = uploadAvatarResponse.fileName,
                            name = uploadAvatarResponse.name
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    Completable.error(
                        Throwable("Save Fail")
                    )
                }
            }

    }

}