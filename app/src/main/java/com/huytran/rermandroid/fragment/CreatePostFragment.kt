package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.esafirm.rximagepicker.RxImagePicker
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.data.local.localbean.RoomData
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.kinda.alert.KAlertDialog
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.create_post.*
import kotlinx.android.synthetic.main.create_post.btnUploadImage
import kotlinx.android.synthetic.main.create_post.etAddress
import kotlinx.android.synthetic.main.create_post.etDescription
import kotlinx.android.synthetic.main.create_post.etFloor
import kotlinx.android.synthetic.main.create_post.etPrepaid
import kotlinx.android.synthetic.main.create_post.etPrice
import kotlinx.android.synthetic.main.create_post.etSquare
import kotlinx.android.synthetic.main.create_post.etTitle
import kotlinx.android.synthetic.main.detail_room.*
import kotlinx.android.synthetic.main.fragment_edit_post.*
import java.util.*
import javax.inject.Inject

class CreatePostFragment() : BaseFragment() {

    @Inject
    lateinit var roomController: RoomController

    private var editedRoom: RoomData? = null
    private val isEdit
        get() = editedRoom != null

    private var imageList: List<Image> = arrayListOf()

    constructor(room: RoomData) : this() {
        this.editedRoom = room
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEdit) initDataForEdit()
        btnPost.visibility = if (isEdit) View.GONE else View.VISIBLE
        btnDelete.visibility = if (isEdit) View.VISIBLE else View.GONE
        btnUpdate.visibility = if (isEdit) View.VISIBLE else View.GONE

        btnUploadImage.setOnClickListener {
            RxImagePicker.getInstance()
                .start(
                    context!!,
                    ImagePicker.create(this)
                        .multi()
                        .origin(ArrayList(imageList))
                )
                .subscribe { list ->
                    list?.let {
                        imageList = it
                    }
                }
        }

        btnPost.setOnClickListener {

            if (etTitle.text.isBlank()
                || etSquare.text.isBlank()
                || etFloor.text.isBlank()
                || etMaxMember.text.isBlank()
                || etAddress.text.isBlank()
                || etPrice.text.isBlank()
                || etPrepaid.text.isBlank()
                || etDescription.text.isBlank()
                || etTerm.text.isBlank()
                || etElectricityPrice.text.isBlank()
                || etWaterPrice.text.isBlank()
            ) {
                KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Missing Field!")
                    .show()
                return@setOnClickListener
            }

            if (imageList.isEmpty()) {
                KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Need Images For Room!")
                    .show()
                return@setOnClickListener
            }

            roomController.createRoom(
                etTitle.text.toString(),
                etSquare.text.toString().toFloat(),
                etAddress.text.toString(),
                etPrice.text.toString().toLong(),
                when (rgRoomType.checkedRadioButtonId) {
                    R.id.radioButtonHome -> 0
                    R.id.radioBtnRoom -> 1
                    R.id.radioBtnDorm -> 2
                    else -> 3
                },
                etFloor.text.toString().toInt(),
                when (rgHasFurniture.checkedRadioButtonId) {
                    R.id.radioBtnHasFurniture -> true
                    R.id.radioBtnNotHasFurniture -> false
                    else -> false
                },
                etMaxMember.text.toString().toInt(),
                when (rgCookingAllowance.checkedRadioButtonId) {
                    R.id.radioBtnAllowCooking -> true
                    else -> false
                },
                when (rgHomeType.checkedRadioButtonId) {
                    R.id.radioButtonHomeTypeSpecial -> 0
                    R.id.radioButtonHomeType1 -> 1
                    R.id.radioButtonHomeType2 -> 2
                    R.id.radioButtonHomeType3 -> 3
                    R.id.radioButtonHomeType4 -> 4
                    else -> 5
                },
                etPrepaid.text.toString().toLong(),
                etDescription.text.toString(),
                imageList,
                etTerm.text.toString(),
                etElectricityPrice.text.toString().toLong(),
                etWaterPrice.text.toString().toLong()
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    disposableContainer.add(it)
                }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        fragmentManager?.popBackStack()
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
        }

        btnUpdate.setOnClickListener {
            if (etTitle.text.isBlank()
                || etSquare.text.isBlank()
                || etFloor.text.isBlank()
                || etMaxMember.text.isBlank()
                || etAddress.text.isBlank()
                || etPrice.text.isBlank()
                || etPrepaid.text.isBlank()
                || etDescription.text.isBlank()
                || etTerm.text.isBlank()
                || etElectricityPrice.text.isBlank()
                || etWaterPrice.text.isBlank()
            ) {
                KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Missing Field!")
                    .show()
                return@setOnClickListener
            }

//            if (imageList.isEmpty()) {
//                KAlertDialog(context, KAlertDialog.ERROR_TYPE)
//                    .setTitleText("Oops...")
//                    .setContentText("Missing Field!")
//                    .show()
//                return@setOnClickListener
//            }

            editedRoom?.let { editedRoom ->
                roomController.updateRoom(
                    editedRoom.id,
                    etTitle.text.toString(),
                    etSquare.text.toString().toFloat(),
                    etAddress.text.toString(),
                    etPrice.text.toString().toLong(),
                    when (rgRoomType.checkedRadioButtonId) {
                        R.id.radioButtonHome -> 0
                        R.id.radioBtnRoom -> 1
                        R.id.radioBtnDorm -> 2
                        else -> 3
                    },
                    etFloor.text.toString().toInt(),
                    when (rgHasFurniture.checkedRadioButtonId) {
                        R.id.radioBtnHasFurniture -> true
                        R.id.radioBtnNotHasFurniture -> false
                        else -> false
                    },
                    etMaxMember.text.toString().toInt(),
                    when (rgCookingAllowance.checkedRadioButtonId) {
                        R.id.radioBtnAllowCooking -> true
                        else -> false
                    },
                    when (rgHomeType.checkedRadioButtonId) {
                        R.id.radioButtonHomeTypeSpecial -> 0
                        R.id.radioButtonHomeType1 -> 1
                        R.id.radioButtonHomeType2 -> 2
                        R.id.radioButtonHomeType3 -> 3
                        R.id.radioButtonHomeType4 -> 4
                        else -> 5
                    },
                    etPrepaid.text.toString().toLong(),
                    etDescription.text.toString(),
                    imageList,
                    etTerm.text.toString(),
                    etElectricityPrice.text.toString().toLong(),
                    etWaterPrice.text.toString().toLong()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        disposableContainer.add(it)
                    }
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            fragmentManager?.popBackStack(
                                RoomDetailFragment::class.java.simpleName,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                        }

                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                    })
            }
        }

        btnDelete.setOnClickListener {
            editedRoom?.let { editedRoom ->
                roomController.deleteRoom(editedRoom.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe {
                        disposableContainer.add(it)
                    }
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            fragmentManager?.popBackStack(
                                RoomDetailFragment::class.java.simpleName,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeSelectedBottomNavigationBaseOnFragment(this)
    }

    private fun initDataForEdit() {
        editedRoom?.let { room ->
            etSquare.setText(room.square.toString())
            etAddress.setText(room.address)
            etPrice.setText(room.price.toString())
            etFloor.setText(room.numberOfFloor.toString())
            etMaxMember.setText(room.maxMember.toString())
            etPrepaid.setText(room.prepaid.toString())
            etDescription.setText(room.description)
            etTerm.setText(room.term)
            etTitle.setText(room.title)
            etWaterPrice.setText(room.waterPrice.toString())
            etElectricityPrice.setText(room.electricityPrice.toString())

            rgRoomType.check(
                when (room.type) {
                    0 -> R.id.radioButtonHome
                    1 -> R.id.radioBtnRoom
                    2 -> R.id.radioBtnDorm
                    else -> R.id.radioButtonHome
                }
            )

            rgHasFurniture.check(
                if (room.hasFurniture) R.id.radioBtnHasFurniture else R.id.radioBtnHasFurniture
            )

            rgCookingAllowance.check(
                if (room.cookingAllowance) R.id.radioBtnAllowCooking else R.id.radioBtnNotAllowCooking
            )

            rgHomeType.check(
                when (room.homeType) {
                    0 -> R.id.radioButtonHomeTypeSpecial
                    1 -> R.id.radioButtonHomeType1
                    2 -> R.id.radioButtonHomeType2
                    3 -> R.id.radioButtonHomeType3
                    4 -> R.id.radioButtonHomeType4
                    else -> R.id.radioButtonHomeTypeSpecial
                }
            )
        }
    }
}