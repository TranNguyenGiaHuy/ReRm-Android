package com.huytran.rermandroid.di.module

import android.content.SharedPreferences
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.UserController
import dagger.Module
import dagger.Provides
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    internal fun provideServerChannel(): ManagedChannel {
        return ManagedChannelBuilder
            .forAddress("10.0.2.2", 6565)
            .usePlaintext()
            .build()
    }

    @Provides
    @Singleton
    internal fun provideUserController(serverChannel: ManagedChannel, privatePreferences: SharedPreferences, userRepository: UserRepository): UserController {
        return UserController(serverChannel, privatePreferences, userRepository)
    }

}