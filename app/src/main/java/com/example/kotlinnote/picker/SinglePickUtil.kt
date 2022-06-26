package com.example.kotlinnote.picker

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import cn.dujc.core.ui.BaseDialog
import cn.dujc.widget.wheelpicker.WheelPicker
import com.example.kotlinnote.R
import java.util.*

class SinglePickUtil(context: Context?) : BaseDialog(context) {
    interface OnSelectedListener {
        fun onSelected(selectedValue: Any?)
    }

    interface OnSelectedPositionListener {
        fun onSelected(selectedValue: Any?, position: Int)
    }

    private var mOnSelectedListener: OnSelectedListener? = null
    private var mOnSelectedPositionListener: OnSelectedPositionListener? = null
    private var mWheelPicker: WheelPicker? = null
    private var mTvSure: TextView? = null
    private var mTvTitle: TextView? = null
    private var mTvCancel: TextView? = null
    private var mTitle: CharSequence? = null
    private var mData: List<*>? = null
    private var mCurrentSelectedData: Any? = null
    private var mPosition = 0
    override fun getViewId(): Int {
        return R.layout.pop_picker
    }

    override fun initBasic(savedInstanceState: Bundle?) {
        mTvSure = findViewById(R.id.tv_sure)
        mTvCancel = findViewById(R.id.tv_cancel)
        mTvTitle = findViewById(R.id.tv_title)
        mWheelPicker = findViewById(R.id.wheelpicker)
        resetData(mData)
        setTitle(mTitle)
        initListener()
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        mTitle = title
        if (mTvTitle != null) mTvTitle?.text = title
    }

    fun selectItemByPosition(position: Int) {
        if (mWheelPicker != null) {
            mWheelPicker?.selectedItemPosition = position
        }
        if (mData?.isNotEmpty() == true) {
            mCurrentSelectedData = mData?.get(position)
            mPosition = position
        }
    }

    fun resetData(data: List<*>?) {
        mData = data
        if (mWheelPicker != null) mWheelPicker?.data = mData
        if (mData?.isNotEmpty() == true) {
            mCurrentSelectedData = mData?.get(0)
            mPosition = 0
        }
    }

    fun resetData(data: Array<String?>) {
        mData = Arrays.asList<String>(*data)
        if (mWheelPicker != null) mWheelPicker?.data = mData
        if (mData?.isNotEmpty() == true) {
            mCurrentSelectedData = mData?.get(0)
            mPosition = 0
        }
    }

    fun setOnSelectedListener(onSelectedListener: OnSelectedListener?) {
        mOnSelectedListener = onSelectedListener
    }

    fun setOnSelectedPositionListener(onSelectedPositionListener: OnSelectedPositionListener?) {
        mOnSelectedPositionListener = onSelectedPositionListener
    }

    private fun initListener() {
        mTvSure?.setOnClickListener {
            if (mOnSelectedListener != null) {
                mOnSelectedListener?.onSelected(mCurrentSelectedData)
            }
            if (mOnSelectedPositionListener != null) {
                mOnSelectedPositionListener?.onSelected(mCurrentSelectedData, mPosition)
            }
            dismiss()
        }
        mTvCancel?.setOnClickListener { dismiss() }
        mWheelPicker?.setOnItemSelectedListener { picker, data, position ->
            mCurrentSelectedData = data
            mPosition = position
        }
    }

    fun setSelectedItem(position: Int) {
        mWheelPicker?.selectedItemPosition = position
        if (mData?.isNotEmpty() == true) {
            mCurrentSelectedData = mData?.get(position)
            mPosition = position
        }
    }
}