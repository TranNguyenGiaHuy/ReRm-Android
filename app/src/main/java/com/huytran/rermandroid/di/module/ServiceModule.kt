package com.huytran.rermandroid.di.module

import com.huytran.rermandroid.manager.ReRmFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector
    abstract fun contributeReRmFirebaseMessagingService(): ReRmFirebaseMessagingService

}