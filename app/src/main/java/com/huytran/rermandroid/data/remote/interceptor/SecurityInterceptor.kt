package com.huytran.rermandroid.data.remote.interceptor

import io.grpc.*
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener
import io.grpc.ForwardingClientCall.SimpleForwardingClientCall

class SecurityInterceptor(val token: String) : ClientInterceptor {

    private val header = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

    override fun <ReqT : Any?, RespT : Any?> interceptCall(
        method: MethodDescriptor<ReqT, RespT>?,
        callOptions: CallOptions?,
        next: Channel?
    ): ClientCall<ReqT, RespT> {
        return object : SimpleForwardingClientCall<ReqT, RespT>(next?.newCall(method, callOptions)) {

            override fun start(responseListener: ClientCall.Listener<RespT>, headers: Metadata) {

                headers.put(header, token)
                super.start(
                    object : SimpleForwardingClientCallListener<RespT>(responseListener) {
                    },
                    headers
                )

            }
        }
    }

}