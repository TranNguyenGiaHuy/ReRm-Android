package com.huytran.rermandroid.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.LoginActivity
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import com.huytran.rermandroid.manager.TransactionManager.Companion.replaceFragmentWithWithBackStack
import io.reactivex.CompletableObserver
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject


class ProfileFragment: BaseFragment() {

    @Inject
    lateinit var userController: UserController

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_profile, container, false)

        val tvUserName = view.findViewById<TextView>(R.id.tv_profile_username)

        userRepository.getLast()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object : MaybeObserver<User> {
                override fun onSuccess(t: User) {
                    tvUserName.text = t.name
                }

                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }

            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_more_profile.setOnClickListener {
            TransactionManager.replaceFragmentWithNoBackStack(
                activity!!,
                ProfileDetailFragment()
            )
        }

        profile_manage_post.setOnClickListener {

        }

        profile_notification.setOnClickListener {

        }

        profile_Logout.setOnClickListener {
            val intent = Intent(this.activity!!, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}
