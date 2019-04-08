package com.huytran.rermandroid.activity

import android.os.Bundle
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import com.huytran.rermandroid.fragment.LoginFragment
import com.huytran.rermandroid.manager.TransactionManager
import javax.inject.Inject

class LoginActivity @Inject constructor() : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        TransactionManager.replaceFragmentWithNoBackStack(this, LoginFragment())
    }

}