package com.huytran.rermandroid.data.remote

import android.content.Context
import com.huytran.grpcdemo.generatedproto.Contract
import com.huytran.grpcdemo.generatedproto.ContractServiceGrpc
import com.huytran.grpcdemo.generatedproto.GetAllContractRequest
import com.huytran.grpcdemo.generatedproto.GetAllContractResponse
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Single

class ContractController(
    private val context: Context,
    private val channel: Channel) {

    fun getAllContract(): Single<List<Contract>> {
        val stub = ContractServiceGrpc.newStub(channel)

        val request = GetAllContractRequest.newBuilder().build()
        return Single.create<List<Contract>> {emitter ->
            val response =  object: StreamObserver<GetAllContractResponse> {
                override fun onNext(value: GetAllContractResponse) {
                    emitter.onSuccess(value.contractList)
                }

                override fun onError(t: Throwable?) {
                    t?.let {
                        t.printStackTrace()
                        emitter.onError(t)
                    }
                }

                override fun onCompleted() {
                }

            }

            stub.getAllContract(request, response)
        }
    }

}