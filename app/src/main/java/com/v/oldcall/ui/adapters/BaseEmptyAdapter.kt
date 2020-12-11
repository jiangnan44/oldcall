package com.v.oldcall.ui.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/12/7
 */
abstract class BaseEmptyAdapter<T, VH : BaseHolder> : BaseAdapter<T> {
    constructor(mContext: Context, layoutId: Int) : super(mContext, layoutId)
    constructor(mContext: Context, items: MutableList<T?>?, layoutId: Int) : super(
        mContext,
        items,
        layoutId
    )


    companion object {
        private const val TAG = "BaseEmptyAdapter"
        private const val VIEW_TYPE_NORMAL = 0x10
        private const val VIEW_TYPE_EMPTY = 0x11
    }


    private var emptyClickListener: View.OnClickListener? = null
    private var btnText: String? = null
    private var tvText: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        if (viewType == VIEW_TYPE_EMPTY) {
            val emptyView =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_empty_view, parent, false)
            return EmptyHolder(emptyView)
        }
        val itemView = LayoutInflater.from(mContext)
            .inflate(mLayoutId, parent, false)
        return getViewHolder(itemView)
    }



    abstract fun bindHolder(holder: VH, position: Int)

    protected fun extendEmptyHolder(holder: BaseEmptyAdapter<*, *>.EmptyHolder, position: Int) {}


    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (holder is BaseEmptyAdapter<*, *>.EmptyHolder) {
            holder.btn.setOnClickListener(emptyClickListener)
            if (!TextUtils.isEmpty(tvText)) {
                holder.tv.text = tvText
            }
            if (!TextUtils.isEmpty(btnText)) {
                holder.btn.text = btnText
            }
            extendEmptyHolder(holder, position)
        } else bindHolder(holder as VH, position)
    }


    inner class EmptyHolder(itemView: View) : BaseHolder(itemView) {
        var tv: TextView = itemView.findViewById(R.id.lev_tv_empty)
        var btn: Button = itemView.findViewById(R.id.lev_btn_empty)
    }

    fun setEmptyButtonClickListener(listener: View.OnClickListener) {
        emptyClickListener = listener
    }

    fun setEmptyTextHint(txt: String) {
        tvText = txt
    }

    fun setEmptyButtonText(txt: String) {
        btnText = txt
    }

    override fun getItemCount(): Int {
        return if (items.size == 0) {
            1
        } else {
            items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.size == 0) {
            VIEW_TYPE_EMPTY
        } else {
            VIEW_TYPE_NORMAL
        }
    }
}