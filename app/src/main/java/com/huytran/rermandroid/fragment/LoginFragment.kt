package com.huytran.rermandroid.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject
import android.text.InputFilter
import android.widget.Toast
import com.huytran.rermandroid.activity.LoginActivity
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.manager.TransactionManager
import dmax.dialog.SpotsDialog
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LoginFragment : BaseFragment() {

    @Inject
    lateinit var userController: UserController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
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
        etUsername.filters = arrayOf(filter)
        etPassword.filters = arrayOf(filter)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().ifEmpty {
                return@setOnClickListener
            }
            val password = etPassword.text.toString().ifEmpty {
                return@setOnClickListener
            }

            val loadingDialog = SpotsDialog.Builder()
                .setContext(context!!)
                .setMessage("Login")
                .build()

            userController.login(
                username,
                password
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    loadingDialog.show()
                }
                .doFinally {
                    loadingDialog.dismiss()
                }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        (activity as LoginActivity).startActivitySilently(MainActivity::class.java)
                    }

                    override fun onSubscribe(d: Disposable) {
                        disposableContainer.add(d)
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }
        btnSignup.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(context!!, SignupFragment())
        }
    }

}