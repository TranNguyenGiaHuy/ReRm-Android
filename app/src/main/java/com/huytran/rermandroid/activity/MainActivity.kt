package com.huytran.rermandroid.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
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

//        val exploreFragment = ExploreFragment.newInstance()
//        openFragment(exploreFragment)
        TransactionManager.replaceFragmentWithNoBackStack(
            this,
            TestFragment()
        )
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_profile -> {
                val profileFragment = ProfileFragment.newInstance()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_explore -> {
                val exploreFragment = ExploreFragment.newInstance()
                openFragment(exploreFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
