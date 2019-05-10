package com.huytran.rermandroid.adapter

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.huytran.rermandroid.R
import com.opensooq.pluto.base.PlutoAdapter
import com.opensooq.pluto.base.PlutoViewHolder
import com.opensooq.pluto.listeners.OnItemClickListener
import java.io.File


class ImageViewAdapter(imageList: List<File>, onItemClickListener: OnItemClickListener<File>, private val doubleClickListener: Listener) :
    PlutoAdapter<File, ImageViewAdapter.ViewHolder>(imageList, onItemClickListener) {

    interface Listener {
        fun doubleClickListener()
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, R.layout.image_view_item, doubleClickListener)
    }


    class ViewHolder(parent: ViewGroup, itemLayoutId: Int, doubleClickListener: Listener) : PlutoViewHolder<File>(parent, itemLayoutId) {

        init {
            GestureDetector(context, GestureDetector.SimpleOnGestureListener()).setOnDoubleTapListener(object: GestureDetector.OnDoubleTapListener{
                
                override fun onDoubleTap(p0: MotionEvent?): Boolean {
                    doubleClickListener.doubleClickListener()
                    return true
                }

                override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
                    return true
                }

                override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
                    return true
                }

            })
        }

        private val imgView: ImageView = getView(R.id.imgView)

        override fun set(item: File, position: Int) {
            Glide.with(imgView)
                .load(item)
                .into(imgView)
        }
    }

}