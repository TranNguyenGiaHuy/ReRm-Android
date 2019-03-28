package com.huytran.rermandroid.di.module

import android.content.Context
import android.content.SharedPreferences
import com.huytran.rermandroid.data.remote.DataController
import com.huytran.rermandroid.di.scope.ApplicationContext
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
    internal fun provideDataController(serverChannel: ManagedChannel, privatePreferences: SharedPreferences): DataController {
        return DataController(serverChannel, privatePreferences)
    }

}