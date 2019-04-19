package com.huytran.rermandroid.data.remote

import com.huytran.grpcdemo.generatedproto.ContractTerm
import com.huytran.grpcdemo.generatedproto.ContractTermServiceGrpc
import com.huytran.grpcdemo.generatedproto.GetAllContractTermRequest
import com.huytran.grpcdemo.generatedproto.GetAllContractTermResponse
import com.huytran.rermandroid.utilities.ResultCode
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Single
import timber.log.Timber

class ContractTermController(
    private val channel: Channel
) {

    fun getAll(): Single<List<ContractTerm>> {
        val stub = ContractTermServiceGrpc.newStub(channel)
        val request = GetAllContractTermRequest.newBuilder().build()

        return Single.create<List<ContractTerm>> { emitter ->
            var contractTermList = emptyList<ContractTerm>()
            val errorThrowable = Throwable("Get ContractTermFail")

            val response = object : StreamObserver<GetAllContractTermResponse> {
                override fun onNext(value: GetAllContractTermResponse?) {
                    value?.let {
                        if (value.resultCode != ResultCode.RESULT_CODE_VALID) {
                            emitter.onError(
                                errorThrowable
                            )
                            Timber.e(errorThrowable, "Error With Code: ${value.resultCode}")
                        } else {
                            contractTermList = value.contractTermListList
                        }
                    }
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                    emitter.onError(
                        errorThrowable
                    )
                }

                override fun onCompleted() {
                    emitter.onSuccess(contractTermList)
                }

            }

            stub.getAllContractTerm(
                request,
                response
            )
        }
    }

}