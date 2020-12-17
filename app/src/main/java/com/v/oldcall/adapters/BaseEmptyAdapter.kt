package com.v.oldcall.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        private const val VIEW_TYPE_EMPTY = Integer.MAX_VALUE - 1
    }


    private var emptyClickListener: View.OnClickListener? = null
    private var btnText: String? = null
    private var tvText: String? = null


    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        if (viewType == VIEW_TYPE_EMPTY) {
            val emptyView =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_empty_view, parent, false)
            return BaseMagicAdapter.EmptyHolder(emptyView)
        }
        val itemView = LayoutInflater.from(mContext)
            .inflate(mLayoutId, parent, false)
        return getRealViewHolder(itemView)
    }


    final override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (holder is BaseMagicAdapter.EmptyHolder) {
            holder.btn.setOnClickListener(emptyClickListener)
            if (!TextUtils.isEmpty(tvText)) {
                holder.tv.text = tvText
            }
            if (!TextUtils.isEmpty(btnText)) {
                holder.btn.text = btnText
            }
            extendEmptyHolder(holder, position)
        } else bindRealHolder(holder as VH, position)
    }

    abstract fun bindRealHolder(holder: VH, position: Int)
    open fun extendEmptyHolder(holder: BaseMagicAdapter.EmptyHolder, position: Int) {}




    fun setEmptyButtonClickListener(listener: View.OnClickListener) {
        emptyClickListener = listener
    }

    fun setEmptyTextHint(txt: String) {
        tvText = txt
    }

    fun setEmptyButtonText(txt: String) {
        btnText = txt
    }

    final override fun getItemCount(): Int {
        return if (items.size == 0) {
            1
        } else {
            items.size
        }
    }

    fun getRealItemCount() = items.size

    final override fun getItemViewType(position: Int): Int {
        if (items.size == 0) {
            return VIEW_TYPE_EMPTY
        }

        return getRealItemViewType(position)
    }

    /**
     * NOTE:don't conflict with @see #VIEW_TYPE_EMPTY
     */
    fun getRealItemViewType(position: Int): Int = 0
}