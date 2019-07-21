package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.huytran.grpcdemo.generatedproto.RentRequest
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.remote.RentRequestController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.kinda.alert.KAlertDialog
import com.savvi.rangedatepicker.CalendarPickerView
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_date_picker.*
import java.util.*
import javax.inject.Inject

class DatePickerFragment(private val roomId: Long, private val isEdit: Boolean) : BaseFragment() {

    @Inject
    lateinit var rentRequestController: RentRequestController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val now = Calendar.getInstance().time
        val maxTimeLimited = Calendar.getInstance()
        maxTimeLimited.set(
            maxTimeLimited.get(Calendar.YEAR) + 10,
            maxTimeLimited.get(Calendar.MONTH),
            maxTimeLimited.get(Calendar.DAY_OF_YEAR)
        )

        calendar_view.init(now, maxTimeLimited.time)
            .inMode(CalendarPickerView.SelectionMode.RANGE)

        btn_confirm.setOnClickListener {
            val from = calendar_view.selectedDates.min()
            val to = calendar_view.selectedDates.max()

            if (calendar_view.selectedDates.size < 2
                || from == null
                || to == null
                || from == to
            ) {
                KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please Pick Date!")
                    .show()
                return@setOnClickListener
            }

            if (isEdit) {
                rentRequestController.update(roomId, from.time, to.time)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        disposableContainer.add(it)
                    }.subscribe(object : SingleObserver<RentRequest> {
                        override fun onSuccess(t: RentRequest) {
                            fragmentManager?.popBackStack(
                                ExploreFragment::class.java.simpleName,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Rent Request Fail!")
                                .show()
                        }
                    })
            } else {
                rentRequestController.addRentRequest(roomId, from.time, to.time)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        disposableContainer.add(it)
                    }.subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            fragmentManager?.popBackStack()
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Rent Request Fail!")
                                .show()
                        }

                    })
            }
        }
    }

}