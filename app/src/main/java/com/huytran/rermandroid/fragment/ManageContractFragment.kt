package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.ManageContractAdapter
import com.huytran.rermandroid.adapter.ManagePostAdapter
import com.huytran.rermandroid.data.local.entity.Contract
import com.huytran.rermandroid.data.local.entity.Room
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_manage_contract.*
import javax.inject.Inject

class ManageContractFragment @Inject constructor() : BaseFragment(){
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_manage_contract, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = "Manage Contract"

        val listContract : ArrayList<Contract> = ArrayList()
        val contract : Contract = Contract(1)
        val contract2 : Contract = Contract(2)
        listContract.add(contract)
        listContract.add(contract2)
        rvContract.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ManageContractAdapter(listContract,this.context)
        }
    }
}