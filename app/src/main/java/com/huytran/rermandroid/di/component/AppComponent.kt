package com.huytran.rermandroid.di.component

import android.app.Application
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.di.AppController
import com.huytran.rermandroid.di.module.ActivityModule
import com.huytran.rermandroid.di.module.ApiModule
import com.huytran.rermandroid.di.module.FragmentModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ActivityModule::class,
        ApiModule::class,
        FragmentModule::class,
        AndroidSupportInjectionModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun apiModule(apiModule: ApiModule): Builder

        fun build(): AppComponent

    }

    fun inject(appController: AppController)
//    fun inject(mainActivity: MainActivity)

}