package com.huytran.rermandroid.di.component

import android.content.Context
import com.huytran.rermandroid.di.AppController
import com.huytran.rermandroid.di.module.ActivityModule
import com.huytran.rermandroid.di.module.ApiModule
import com.huytran.rermandroid.di.module.FragmentModule
import com.huytran.rermandroid.di.module.SharedPreferenceModule
import com.huytran.rermandroid.di.scope.ApplicationContext
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ActivityModule::class,
        ApiModule::class,
        FragmentModule::class,
        AndroidSupportInjectionModule::class,
        SharedPreferenceModule::class
    ]
)
interface AppComponent : AndroidInjector<AppController> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<AppController>() {

        @BindsInstance
        abstract fun apiModule(apiModule: ApiModule): Builder

        @BindsInstance
        abstract fun appContext(@ApplicationContext context: Context)

        @BindsInstance
        abstract fun sharedPreferenceModule(sharedPreferenceModule: SharedPreferenceModule): Builder

        override fun seedInstance(instance: AppController?) {
            appContext(instance as Context)
        }

    }

    override fun inject(appController: AppController)
//    fun inject(mainActivity: MainActivity)

}