package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.fragment.base.BaseFragment
import io.reactivex.MaybeObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileDetailFragment: BaseFragment(){

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.detail_profile, container, false)

        val tvUserName = view.findViewById<TextView>(R.id.tvUsername)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvIdCard = view.findViewById<TextView>(R.id.tvID)
        val tvIdCardDated = view.findViewById<TextView>(R.id.tv_Date_Of_Id)
        val tvDateOfBirth = view.findViewById<TextView>(R.id.tv_Date_Of_Birth)
        val tvPlaceOfPermanent = view.findViewById<TextView>(R.id.tv_place_of_permanent)

        userRepository.getLast()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object : MaybeObserver<User> {
                override fun onSuccess(t: User) {
                    tvUserName.text = t.name
                    tvName.text = t.userName
                    tvPhone.text = t.phoneNumber
                    tvIdCard.text = t.idCard
                    tvIdCardDated.text = t.tsCardDated.toString()
                    tvDateOfBirth.text = t.tsDateOfBirth.toString()
                    tvName.text = t.userName
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
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}