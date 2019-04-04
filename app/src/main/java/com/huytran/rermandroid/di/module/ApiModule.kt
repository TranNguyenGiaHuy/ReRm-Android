package com.huytran.rermandroid.di.module

import android.content.Context
import android.content.SharedPreferences
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.data.remote.interceptor.SecurityInterceptor
import com.huytran.rermandroid.di.scope.ApplicationContext
import dagger.Module
import dagger.Provides
import io.grpc.Channel
import io.grpc.ClientInterceptors
import io.grpc.ManagedChannel
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
        val token = context.getSharedPreferences("Private", Context.MODE_PRIVATE).getString("session", "") ?: ""

        if (token.isNotEmpty()) {
            return ClientInterceptors.intercept(
                channel,
                SecurityInterceptor(token)
            )
        }

        return channel
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

}