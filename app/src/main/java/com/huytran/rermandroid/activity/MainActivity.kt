package com.huytran.rermandroid.activity

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import com.huytran.rermandroid.fragment.ExploreFragment
import com.huytran.rermandroid.fragment.ProfileFragment
import com.huytran.rermandroid.fragment.TestFragment
import com.huytran.rermandroid.manager.TransactionManager

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation : BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        TransactionManager.replaceFragmentWithNoBackStack(
            this,
            TestFragment()
        )
//        TransactionManager.replaceFragmentWithNoBackStack(
//            this,
//            ExploreFragment()
//        )
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_profile -> {
                TransactionManager.replaceFragmentWithWithBackStack(
                    this,
                    ProfileFragment()
                )
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_explore -> {
                TransactionManager.replaceFragmentWithWithBackStack(
                    this,
                    ExploreFragment()
                )
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
