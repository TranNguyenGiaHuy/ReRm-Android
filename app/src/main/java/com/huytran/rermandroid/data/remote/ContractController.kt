package com.huytran.rermandroid.data.remote

import android.content.Context
import com.huytran.grpcdemo.generatedproto.*
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Completable
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

    fun terminnate(id: Long): Completable {
        val stub = ContractServiceGrpc.newStub(channel)

        val request = TerminateContractRequest.newBuilder().setId(id).build()
        return Completable.create {emitter ->
            val response = object: StreamObserver<TerminateContractResponse> {
                override fun onNext(value: TerminateContractResponse?) {
                }

                override fun onError(t: Throwable?) {
                    t?.let {
                        t.printStackTrace()
                        emitter.onError(t)
                    }
                }

                override fun onCompleted() {
                    emitter.onComplete()
                }
            }

            stub.terminateContract(
                request,
                response
            )
        }
    }

}