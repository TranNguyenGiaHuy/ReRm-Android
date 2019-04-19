package com.huytran.rermandroid.activity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.base.BaseActivity
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.CreatePostFragment
import com.huytran.rermandroid.fragment.ExploreFragment
import com.huytran.rermandroid.fragment.ProfileDetailFragment
import com.huytran.rermandroid.fragment.ProfileFragment
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var userController: UserController

    private lateinit var bottomNavigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        userController.getUserInfo()
            .observeOn(Schedulers.newThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe()

        TransactionManager.replaceFragmentWithNoBackStack(
            this,
            ExploreFragment()
        )
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

    fun changeSelectedBottomNavigationBaseOnFragment(fragment: Fragment) {
        when (fragment) {
            is ProfileDetailFragment, is ProfileFragment -> bottomNavigation.menu.findItem(R.id.navigation_profile).isChecked = true
            is ExploreFragment, is CreatePostFragment -> bottomNavigation.menu.findItem(R.id.navigation_explore).isChecked = true
        }
    }
}
