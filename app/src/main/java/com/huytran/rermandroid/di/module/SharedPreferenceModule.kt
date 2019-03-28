package com.huytran.rermandroid.di.module

import android.content.Context
import android.content.SharedPreferences
import com.huytran.rermandroid.di.scope.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class SharedPreferenceModule {

    @Provides
    @Singleton
    @Inject
    internal fun providePrivatePreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("Private", Context.MODE_PRIVATE)
    }

}