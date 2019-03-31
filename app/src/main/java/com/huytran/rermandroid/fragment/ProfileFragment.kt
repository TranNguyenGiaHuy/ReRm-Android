package com.huytran.rermandroid.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView

import com.huytran.rermandroid.R
import com.huytran.rermandroid.fragment.base.BaseFragment


class ProfileFragment : BaseFragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment();
    }
}
