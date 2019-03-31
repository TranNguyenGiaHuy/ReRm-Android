package com.huytran.rermandroid.di.module

import com.huytran.rermandroid.fragment.ExploreFragment
import com.huytran.rermandroid.fragment.ProfileFragment
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.fragment.TestFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeBaseFragment(): BaseFragment

    @ContributesAndroidInjector
    abstract fun contributeTestFragment(): TestFragment

    @ContributesAndroidInjector
    abstract fun contributeExploreFragment(): ExploreFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

}