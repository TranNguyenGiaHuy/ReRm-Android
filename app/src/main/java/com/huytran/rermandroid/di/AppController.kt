package com.huytran.rermandroid.di

import com.huytran.rermandroid.di.component.DaggerAppComponent
import com.huytran.rermandroid.di.module.ApiModule
import com.huytran.rermandroid.di.module.DatabaseModule
import com.huytran.rermandroid.di.module.SharedPreferenceModule
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.DaggerApplication
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber

class AppController: DaggerApplication(), HasActivityInjector, HasSupportFragmentInjector {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder()
            .apiModule(ApiModule())
            .databaseModule(DatabaseModule())
            .sharedPreferenceModule(SharedPreferenceModule())
            .create(this)
    }
}