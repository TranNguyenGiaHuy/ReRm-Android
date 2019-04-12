package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.LoginActivity
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.kinda.alert.KAlertDialog
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_signup_simple.*
import javax.inject.Inject

class SignupFragment : BaseFragment() {

    @Inject
    lateinit var userController: UserController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_simple, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (!Character.isLetterOrDigit(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        etName.filters = arrayOf(filter)
        etPassword.filters = arrayOf(filter)
        etPasswordConfirm.filters = arrayOf(filter)

        btnConfirm.setOnClickListener {
            if (etName.text.toString().isBlank()
                || etPassword.text.toString().isBlank()
                || etPasswordConfirm.text.toString().isBlank()
            ) {
                Toast.makeText(context!!, "Empty Field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (etPassword.text.toString() != etPasswordConfirm.text.toString()) {
                Toast.makeText(context!!, "Confirm Password Not Match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loadingDialog = KAlertDialog(context, KAlertDialog.PROGRESS_TYPE)
            loadingDialog.setTitleText("Sign Up")
                .setCancelable(false)

            userController.signup(
                etName.text.toString(),
                etPassword.text.toString()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingDialog.show()
                }
                .doFinally {
                    loadingDialog.dismiss()
                }
                .subscribe(object: CompletableObserver {
                    override fun onComplete() {
                        (activity as LoginActivity).startActivitySilently(MainActivity::class.java)
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposableContainer.add(d)
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let {error ->
                            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

    }

}