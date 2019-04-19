package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.datePicker
import com.afollestad.materialdialogs.input.input
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.rximagepicker.RxImagePicker
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.data.remote.UserController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.huytran.rermandroid.utilities.UtilityFunctions
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import javax.inject.Inject

class ProfileDetailFragment : BaseFragment() {

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var avatarController: AvatarController
    @Inject
    lateinit var avatarRepository: AvatarRepository
    @Inject
    lateinit var userController: UserController

    private lateinit var tvUserName: TextView
    private lateinit var tvName: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvIdCard: TextView
    private lateinit var tvIdCardDated: TextView
    private lateinit var tvDateOfBirth: TextView
    private lateinit var tvPlaceOfPermanent: TextView
    private lateinit var tvUploadAvatar: TextView

    private lateinit var user: User

    private val simpleDateFormat = "dd/MM/yyyy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.detail_profile, container, false)

        tvUserName = view.findViewById(R.id.tvUsername)
        tvName = view.findViewById(R.id.tvName)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvIdCard = view.findViewById(R.id.tvID)
        tvIdCardDated = view.findViewById(R.id.tv_Date_Of_Id)
        tvDateOfBirth = view.findViewById(R.id.tv_Date_Of_Birth)
        tvPlaceOfPermanent = view.findViewById(R.id.tv_place_of_permanent)
        val ivAvatar = view.findViewById<ImageView>(R.id.img_profile_image)
        tvUploadAvatar = view.findViewById(R.id.tv_upload_avatar)

        val userInfoDisposable = userRepository.getLastFlowable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { user ->
                this.user = user

                tvUserName.text = user.name
                tvName.text = user.userName
                tvPhone.text = user.phoneNumber
                tvIdCard.text = user.idCard
                tvIdCardDated.text = if (user.tsCardDated == 0L) "" else UtilityFunctions.timestampToString(user.tsCardDated)
                tvDateOfBirth.text = if (user.tsDateOfBirth == 0L) "" else UtilityFunctions.timestampToString(user.tsDateOfBirth)
                tvName.text = user.userName
                tvPlaceOfPermanent.text = user.placeOfPermanent
            }
        disposableContainer.add(userInfoDisposable)

        val avatarDisposable = avatarRepository.getLastFlowable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                val file = File(context!!.filesDir, it.fileName)
                Glide
                    .with(ivAvatar)
                    .load(file)
                    .into(ivAvatar)
            }
        disposableContainer.add(avatarDisposable)

        configButton()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivAvatar = view.findViewById<CircleImageView>(R.id.img_profile_image)

        tvUploadAvatar.setOnClickListener {
            RxImagePicker.getInstance()
                .start(
                    context,
                    ImagePicker.create(this)
                        .single()
                )
                .subscribe { imageList ->
                    if (imageList == null) return@subscribe
                    val image = imageList.firstOrNull { image ->
                        image != null
                    } ?: return@subscribe

                    avatarController.uploadAvatar(image.path, image.name)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe {
                            disposableContainer.add(it)
                        }.subscribe(object : CompletableObserver {
                            override fun onComplete() {
                                Glide
                                    .with(ivAvatar)
                                    .load(image.path)
                                    .into(ivAvatar)
                            }

                            override fun onSubscribe(d: Disposable) {
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }

                        })
                }
        }
    }

    private fun updateUser() : Completable {
        return userController.updateInfo(
            User(
                id = user.id,
                svId = user.svId,
                name = tvUserName.text.toString(),
                userName = tvName.text.toString(),
                phoneNumber = tvPhone.text.toString(),
                idCard = tvIdCard.text.toString(),
                tsCardDated = UtilityFunctions.stringToTimestamp(tvIdCardDated.text.toString()) ?: user.tsCardDated,
                tsDateOfBirth = UtilityFunctions.stringToTimestamp(tvDateOfBirth.text.toString()) ?: user.tsDateOfBirth,
                placeOfPermanent = tvPlaceOfPermanent.text.toString(),
                avatarId = user.avatarId
            )
        )
    }

    private fun callUpdate() {
        updateUser()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.addAll(it)
            }
            .subscribe()
    }

    private fun configButton() {
//        tvUserName.setOnClickListener {
//            MaterialDialog(context!!).show {
//                input { _, charSequence ->
//                    tvUserName.text = charSequence
//                    callUpdate()
//                }
//            }
//        }
        tvName.setOnClickListener {
            MaterialDialog(context!!).show {
                input(prefill = tvName.text) { _, charSequence ->
                    if (charSequence.isBlank()) return@input
                    tvName.text = charSequence
                    callUpdate()
                }
                title(text = "What's Your Name?")
                negativeButton(R.string.text_cancel)
            }
        }
        tvPhone.setOnClickListener {
            MaterialDialog(context!!).show {
                input(prefill = tvPhone.text) { _, charSequence ->
                    if (charSequence.isBlank()) return@input
                    tvPhone.text = charSequence
                    callUpdate()
                }
                title(text = "What's Your Phone Number?")
                negativeButton(R.string.text_cancel)
            }
        }
        tvIdCard.setOnClickListener {
            MaterialDialog(context!!).show {
                input(prefill = tvIdCard.text) { _, charSequence ->
                    if (charSequence.isBlank()) return@input
                    tvIdCard.text = charSequence
                    callUpdate()
                }
                title(text = "What's Your Identity Card Number?")
                negativeButton(R.string.text_cancel)
            }
        }
        tvPlaceOfPermanent.setOnClickListener {
            MaterialDialog(context!!).show {
                input(prefill = tvPlaceOfPermanent.text) { _, charSequence ->
                    if (charSequence.isBlank()) return@input
                    tvPlaceOfPermanent.text = charSequence
                    callUpdate()
                }
                title(text = "Where's Your Place Of Permanent?")
                negativeButton(R.string.text_cancel)
            }
        }

        tvIdCardDated.setOnClickListener {
            val displayCalendar = Calendar.getInstance()
            UtilityFunctions.stringToTimestamp(tvIdCardDated.text.toString())?.let {
                displayCalendar.timeInMillis = it
            }

            MaterialDialog(context!!).show {
                datePicker(currentDate = displayCalendar) { _, datetime ->
                    tvIdCardDated.text = UtilityFunctions.timestampToString(datetime.timeInMillis)
                    callUpdate()
                }
                title(text = "When's Your Identity Card Made?")
                negativeButton(R.string.text_cancel)
            }
        }
        tvDateOfBirth.setOnClickListener {
            val displayCalendar = Calendar.getInstance()
            UtilityFunctions.stringToTimestamp(tvDateOfBirth.text.toString())?.let {
                displayCalendar.timeInMillis = it
            }

            MaterialDialog(context!!).show {
                datePicker(currentDate = displayCalendar) { _, datetime ->
                    tvDateOfBirth.text = UtilityFunctions.timestampToString(datetime.timeInMillis)
                    callUpdate()
                }
                title(text = "What's Your Date Of Birth?")
                negativeButton(R.string.text_cancel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeSelectedBottomNavigationBaseOnFragment(this)
    }
}