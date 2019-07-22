package com.huytran.rermandroid.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.LoginActivity
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.data.local.entity.Avatar
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.manager.TransactionManager
import io.reactivex.Completable
import io.reactivex.MaybeObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File
import javax.inject.Inject


class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var userController: UserController

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var avatarRepository: AvatarRepository

    @Inject
    lateinit var avatarController: AvatarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val tvUserName = view.findViewById<TextView>(R.id.tv_profile_username)
        val ivAvatar = view.findViewById<ImageView>(R.id.img_profile_image)

        userRepository.getLastMaybe()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }.flatMapSingle { user ->
                avatarController.getAvatarOfUser(user.svId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
            }.flatMapCompletable { file ->
                Completable.create {emitter ->
                    Glide
                        .with(ivAvatar)
                        .load(file)
                        .into(ivAvatar)
                    emitter.onComplete()
                }
            }.doOnError {
                it.printStackTrace()
            }
            .subscribe()

//        avatarController.getAvatar()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .doOnSubscribe {
//                disposableContainer.add(it)
//            }
//            .subscribe(object: SingleObserver<File> {
//                override fun onSuccess(t: File) {
//                    Glide
//                        .with(ivAvatar)
//                        .load(t)
//                        .into(ivAvatar)
//                }
//
//                override fun onSubscribe(d: Disposable) {
//                }
//
//                override fun onError(e: Throwable) {
//                    e.printStackTrace()
//                }
//
//            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_more_profile.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                activity!!,
                ProfileDetailFragment()
            )
        }

        profile_manage_post.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                activity!!,
                ManagePostFragment()
            )
        }

        profile_manage_contract.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                activity!!,
                ManageContractFragment()
            )
        }

        profile_notification.setOnClickListener {
            TransactionManager.replaceFragmentWithWithBackStack(
                activity!!,
                NotificationFragment()
            )
        }

        profile_Logout.setOnClickListener {
            //            val intent = Intent(this.activity!!, LoginActivity::class.java)
//            startActivity(intent)
//            activity?.finish()
            userController.logout()
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    disposableContainer.add(it)
                }
                .subscribe {
                    (activity as MainActivity).startActivitySilently(LoginActivity::class.java)
                }
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeSelectedBottomNavigationBaseOnFragment(this)
    }
}
