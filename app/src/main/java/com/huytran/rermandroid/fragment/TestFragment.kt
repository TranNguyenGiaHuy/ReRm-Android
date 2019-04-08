package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.test_fragment.*
import javax.inject.Inject

class TestFragment @Inject constructor() : BaseFragment() {

//    @Inject
//    lateinit var serverChannel: ManagedChannel

    @Inject
    lateinit var userController: UserController

    @Inject
    lateinit var userRepository: UserRepository

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSignup.setOnClickListener {
            userController.signup(
                etUsername.text.toString(),
                etPassword.text.toString()
            )
        }

        btnLogin.setOnClickListener {
            userController.login(
                etUsername.text.toString(),
                etPassword.text.toString()
            )
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {

                }
        }

        btnLogout.setOnClickListener {
            userController.logout()
        }

        btnGetUserInfo.setOnClickListener {
            userController.getUserInfo()
        }

        userRepository.getLast()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                txtName.text = it.name
            }.addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}