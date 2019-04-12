package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.rximagepicker.RxImagePicker
import com.huytran.rermandroid.R
import com.huytran.rermandroid.data.local.entity.Avatar
import com.huytran.rermandroid.data.local.entity.User
import com.huytran.rermandroid.data.local.repository.AvatarRepository
import com.huytran.rermandroid.data.local.repository.UserRepository
import com.huytran.rermandroid.data.remote.AvatarController
import com.huytran.rermandroid.fragment.base.BaseFragment
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.CompletableObserver
import io.reactivex.MaybeObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.detail_profile.*
import java.io.File
import javax.inject.Inject

class ProfileDetailFragment : BaseFragment() {

    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var avatarController: AvatarController
    @Inject
    lateinit var avatarRepository: AvatarRepository

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
        val ivAvatar = view.findViewById<ImageView>(R.id.img_profile_image)

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

        avatarRepository.getAll()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                disposableContainer.add(it)
            }
            .subscribe(object: SingleObserver<List<Avatar>> {
                override fun onSuccess(t: List<Avatar>) {
                    if (t.isEmpty()) {
                        onError(
                            Throwable(
                                "Empty"
                            )
                        )
                        return
                    }
                    val file = File(context!!.filesDir, t.last().fileName)
                    Glide
                        .with(ivAvatar)
                        .load(file)
                        .into(ivAvatar)
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivAvatar = view.findViewById<CircleImageView>(R.id.img_profile_image)

        tv_upload_avatar.setOnClickListener {
            RxImagePicker.getInstance()
                .start(
                    context,
                    ImagePicker.create(this)
                        .single()
                )
                .subscribe {imageList ->
                    if (imageList == null) return@subscribe
                    val image = imageList.firstOrNull { image ->
                        image != null
                    } ?: return@subscribe

                    avatarController.uploadAvatar(image.path, image.name)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe {
                            disposableContainer.add(it)
                        }.subscribe(object: CompletableObserver {
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
}