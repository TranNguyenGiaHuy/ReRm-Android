package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.huytran.grpcdemo.generatedproto.Contract
import com.huytran.rermandroid.R
import com.huytran.rermandroid.adapter.ManageContractAdapter
import com.huytran.rermandroid.data.remote.ContractController
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_manage_contract.*
import javax.inject.Inject

class ManageContractFragment: BaseFragment(){

    @Inject
    lateinit var contractController: ContractController
    @Inject
    lateinit var roomController: RoomController

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

        rvContract.apply {
            layoutManager = LinearLayoutManager(activity)
        }

        contractController.getAllContract()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object: SingleObserver<List<Contract>> {
                override fun onSuccess(t: List<Contract>) {
                    rvContract.adapter = ManageContractAdapter(
                        t.toList(),
                        context!!,
                        roomController
                    )
                    rvContract.adapter?.notifyDataSetChanged()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

    }
}