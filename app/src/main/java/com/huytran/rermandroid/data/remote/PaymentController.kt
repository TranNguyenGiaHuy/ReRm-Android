package com.huytran.rermandroid.data.remote

import android.content.Context
import com.huytran.grpcdemo.generatedproto.*
import io.grpc.Channel
import io.grpc.stub.StreamObserver
import io.reactivex.Single

class PaymentController(
    private val context: Context,
    private val channel: Channel
) {

    fun getPaymentOfRoom(roomId: Long): Single<List<Payment>> {
        val stub = PaymentServiceGrpc.newStub(channel)
        val request = GetPaymentOfRoomRequest.newBuilder()
            .setRoomId(roomId)
            .build()
        return Single.create<List<Payment>> {emitter ->
            val response = object: StreamObserver<GetPaymentOfRoomResponse> {
                override fun onNext(value: GetPaymentOfRoomResponse) {
                    emitter.onSuccess(value.paymentList)
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

            stub.getPaymentOfRoom(request, response)
        }
    }

    fun addBill(paymentId: Long, electricityBill: Long, waterBill: Long): Single<Payment> {
        val stub = PaymentServiceGrpc.newStub(channel)
        val request = AddBillRequest.newBuilder()
            .setPaymentId(paymentId)
            .setElectricityBill(electricityBill)
            .setWaterBill(waterBill)
            .build()
        return Single.create<Payment> {emitter ->
            val response = object: StreamObserver<AddBillResponse> {
                override fun onNext(value: AddBillResponse) {
                    emitter.onSuccess(value.payment)
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

            stub.addBill(request, response)
        }
    }

    fun requestPaid(paymentId: Long): Single<Payment> {
        val stub = PaymentServiceGrpc.newStub(channel)
        val request = RequestPaidRequest.newBuilder()
            .setPaymentId(paymentId)
            .build()
        return Single.create<Payment> {emitter ->
            val response = object: StreamObserver<RequestPaidResponse> {
                override fun onNext(value: RequestPaidResponse) {
                    emitter.onSuccess(value.payment)
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

            stub.requestPaid(request, response)

        }
    }

    fun confirmPayment(paymentId: Long): Single<Payment> {
        val stub = PaymentServiceGrpc.newStub(channel)
        val request = ConfirmPaymentRequest.newBuilder()
            .setPaymentId(paymentId)
            .build()
        return Single.create<Payment> {emitter ->
            val response = object: StreamObserver<ConfirmPaymentResponse> {
                override fun onNext(value: ConfirmPaymentResponse) {
                    emitter.onSuccess(value.payment)
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

            stub.confirmPayment(request, response)

        }
    }

}