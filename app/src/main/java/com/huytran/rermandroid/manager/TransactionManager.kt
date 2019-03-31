package com.huytran.rermandroid.manager

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.huytran.rermandroid.R
import com.huytran.rermandroid.fragment.base.BaseFragment

class TransactionManager {

    companion object {

        fun replaceFragmentWithNoBackStack(context: Context, desFragment: BaseFragment) {
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, desFragment, desFragment::class.java.simpleName)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .addToBackStack(null)
                .commit()
        }

        fun replaceFragmentWithWithBackStack(context: Context, desFragment: BaseFragment) {
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.container, desFragment, desFragment::class.java.simpleName)
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .addToBackStack(desFragment::class.java.simpleName)
                .commit()
        }

    }

}