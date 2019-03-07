package com.huytran.rermandroid.activity

import android.os.Bundle
import android.os.PersistableBundle
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity

class TestActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
    }

}