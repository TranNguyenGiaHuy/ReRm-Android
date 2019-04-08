package com.huytran.rermandroid.di.module

import com.huytran.rermandroid.activity.LauncherActivity
import com.huytran.rermandroid.activity.LoginActivity
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.activity.base.BaseActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeBaseActivity(): BaseActivity

    @ContributesAndroidInjector
    abstract fun contributeLauncherActivity(): LauncherActivity

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeLoginActivity(): LoginActivity

}