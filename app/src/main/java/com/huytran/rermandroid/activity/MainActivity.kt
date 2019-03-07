package com.huytran.rermandroid.activity

import android.os.Bundle
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
