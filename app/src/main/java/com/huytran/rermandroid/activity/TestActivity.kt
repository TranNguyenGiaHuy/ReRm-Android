package com.huytran.rermandroid.activity

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import com.huytran.rermandroid.fragment.TestFragment
import com.huytran.rermandroid.manager.TransactionManager

class TestActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        TransactionManager.replaceFragmentWithNoBackStack(this, TestFragment())
        return super.onCreateView(name, context, attrs)

    }

}