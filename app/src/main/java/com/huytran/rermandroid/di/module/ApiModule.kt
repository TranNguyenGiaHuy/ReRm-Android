package com.huytran.rermandroid.di.module

import android.content.Context
import android.content.SharedPreferences
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.ContractTermController
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.data.remote.interceptor.SecurityInterceptor
import com.huytran.rermandroid.di.scope.ApplicationContext
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
            .forAddress("10.0.2.2", 6565)
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
    internal fun provideRoomController(channel: Channel) : RoomController {
        return RoomController(channel)
    }

    @Provides
    @Singleton
    internal fun provideContractTermController(channel: Channel) : ContractTermController {
        return ContractTermController(channel)
    }

}