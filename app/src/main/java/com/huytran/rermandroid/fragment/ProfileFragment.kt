package com.huytran.rermandroid.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.LoginActivity
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.huytran.rermandroid.manager.TransactionManager.Companion.replaceFragmentWithWithBackStack
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject


class ProfileFragment @Inject constructor() : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_more_profile.setOnClickListener {
            TransactionManager.replaceFragmentWithNoBackStack(
                this!!.activity!!,
                ProfileDetailFragment()
            )
        }

        profile_manage_post.setOnClickListener {

        }

        profile_notification.setOnClickListener {

        }

        profile_Logout.setOnClickListener {
            val intent = Intent(this!!.activity!!, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
