package com.huytran.rermandroid.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.esafirm.rximagepicker.RxImagePicker
import com.huytran.rermandroid.R
import com.huytran.rermandroid.activity.MainActivity
import com.huytran.rermandroid.adapter.SelectContractTermAdapter
import com.huytran.rermandroid.data.remote.ContractTermController
import com.huytran.rermandroid.data.remote.RoomController
import com.huytran.rermandroid.fragment.base.BaseFragment
import com.kinda.alert.KAlertDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.create_post.*
import java.util.ArrayList
import javax.inject.Inject

class CreatePostFragment : BaseFragment() {

    @Inject
    lateinit var contractTermController: ContractTermController

    @Inject
    lateinit var roomController: RoomController

    private var imageList: List<Image> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

//        btnViewContractTerm.setOnClickListener {
//            contractTermController.getAll()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .doOnSubscribe {
//                    disposableContainer.add(it)
//                }.subscribe { contractTermList ->
//                    MaterialDialog(context!!).show {
//                        customListAdapter(
//                            SelectContractTermAdapter(context, contractTermList)
//                        )
//                    }
//                }
//        }

        btnPost.setOnClickListener {

            if (etTitle.text.isBlank()
                || etSquare.text.isBlank()
                || etFloor.text.isBlank()
                || etAddress.text.isBlank()
                || etPrice.text.isBlank()
                || etPrepaid.text.isBlank()
                || etDescription.text.isBlank()
            ) {
                KAlertDialog(context, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Missing Field!")
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
                0,
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
                imageList
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    disposableContainer.add(it)
                }
                .subscribe {
                    fragmentManager?.popBackStack()
                }
        }


    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).changeSelectedBottomNavigationBaseOnFragment(this)
    }
}