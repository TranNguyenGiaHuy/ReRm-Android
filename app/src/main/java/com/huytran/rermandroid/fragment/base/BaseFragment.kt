package com.huytran.rermandroid.fragment.base

import android.os.Bundle
import android.support.v4.app.Fragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.*

open class BaseFragment: Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.clearFindViewByIdCache()
    }
}