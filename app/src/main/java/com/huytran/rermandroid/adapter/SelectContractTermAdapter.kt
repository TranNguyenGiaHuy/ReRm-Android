package com.huytran.rermandroid.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huytran.grpcdemo.generatedproto.ContractTerm
import com.huytran.rermandroid.R
import kotlinx.android.synthetic.main.adapter_select_contract_term.view.*

class SelectContractTermAdapter(private val context: Context, private val contractTermList: List<ContractTerm>): RecyclerView.Adapter<SelectContractTermAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_select_contract_term, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return contractTermList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val tvTitle = view.tvTitle
        val tvDescription = view.tvDescription

    }

}