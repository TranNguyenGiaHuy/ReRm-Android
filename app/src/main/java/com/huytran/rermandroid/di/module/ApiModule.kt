package com.huytran.rermandroid.di.module

import android.content.Context
import android.content.SharedPreferences
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.*
import com.huytran.rermandroid.data.remote.interceptor.SecurityInterceptor
import com.huytran.rermandroid.di.scope.ApplicationContext
import com.huytran.rermandroid.utilities.AppConstants.Companion.SERVER_ADDRESS
import dagger.Module
import dagger.Provides
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannelBuilder
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    internal fun provideChannel(@ApplicationContext context: Context): Channel {
        val channel = ManagedChannelBuilder
            .forAddress(SERVER_ADDRESS, 6565)
            .usePlaintext()
            .build()
        return ClientInterceptors.intercept(
            channel,
            SecurityInterceptor(context.getSharedPreferences("Private", Context.MODE_PRIVATE))
        )
    }

    @Provides
    @Singleton
    internal fun provideUserController(
        channel: Channel,
        privatePreferences: SharedPreferences,
        userRepository: UserRepository
    ): UserController {
        return UserController(channel, privatePreferences, userRepository)
    }

    @Provides
    @Singleton
    internal fun provideAvatarController(
        @ApplicationContext context: Context,
        channel: Channel,
        privatePreferences: SharedPreferences,
        userRepository: UserRepository,
        avatarRepository: AvatarRepository
    ): AvatarController {
        return AvatarController(context, channel, privatePreferences, userRepository, avatarRepository)
    }

    @Provides
    @Singleton
    internal fun provideRoomController(
        channel: Channel,
        imageController: ImageController
    ): RoomController {
        return RoomController(channel, imageController)
    }

    @Provides
    @Singleton
    internal fun provideContractTermController(channel: Channel): ContractTermController {
        return ContractTermController(channel)
    }

    @Provides
    @Singleton
    internal fun provideImageController(
        @ApplicationContext context: Context,
        channel: Channel
    ): ImageController {
        return ImageController(context, channel)
    }

    @Provides
    @Singleton
    internal fun provideSavedRoomController(
        @ApplicationContext context: Context,
        channel: Channel
    ): SavedRoomController {
        return SavedRoomController(context, channel)
    }

    @Provides
    @Singleton
    internal fun provideMessageController(
        @ApplicationContext context: Context,
        channel: Channel
    ): MessageController {
        return MessageController(context, channel)
    }

    @Provides
    @Singleton
    internal fun provideRentRequestController(
        @ApplicationContext context: Context,
        channel: Channel
    ): RentRequestController {
        return RentRequestController(context, channel)
    }

    @Provides
    @Singleton
    internal fun providePaymentController(
        @ApplicationContext context: Context,
        channel: Channel
    ): PaymentController {
        return PaymentController(context, channel)
    }

}