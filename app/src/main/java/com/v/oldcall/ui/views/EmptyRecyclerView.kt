package com.v.oldcall.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/12/1
 */
class EmptyRecyclerView : RecyclerView {
    private val TAG = "EmptyRecyclerView"

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var mEmptyView: ViewGroup? = null
    var mEmptyButton: Button? = null
    var mClickListener: OnClickListener? = null



    fun setEmptyView(emptyView: ViewGroup) {
        mEmptyView = emptyView
        mEmptyButton = emptyView.findViewById(R.id.lev_btn_empty)
    }

    fun setOnEmptyTextClickListener(listener: OnClickListener) {
        mClickListener = listener
        mEmptyButton?.setOnClickListener(listener)
    }


    private val emptyObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if (mEmptyView == null) {
                return
            }
            adapter?.let {
                if (it.itemCount == 0) {
                    mEmptyView?.visibility = View.VISIBLE
                    visibility = View.GONE
                } else {
                    mEmptyView?.visibility = View.GONE
                    visibility = View.VISIBLE
                }
            }
        }
    }


    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        emptyObserver.onChanged()
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mEmptyView?.let {
            it.removeAllViews()
            mEmptyView = null
        }

        if (mClickListener != null) {
            mClickListener = null
        }
    }

}