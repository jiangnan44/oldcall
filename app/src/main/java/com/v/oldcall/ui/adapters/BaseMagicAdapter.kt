package com.v.oldcall.ui.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.collection.SparseArrayCompat
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/12/15
 */
abstract class BaseMagicAdapter<T, VH : BaseHolder> : BaseAdapter<T> {
    constructor(mContext: Context, layoutId: Int) : super(mContext, layoutId)
    constructor(mContext: Context, items: MutableList<T?>?, layoutId: Int) : super(
        mContext,
        items,
        layoutId
    )

    companion object {
        private const val BASE_ITEM_TYPE_HEADER = 100_000
        private const val BASE_ITEM_TYPE_FOOTER = 200_000
        private const val BASE_ITEM_TYPE_EMPTY = 300_000
    }

    private val mHeaders = SparseArrayCompat<View>()
    private val mFooters = SparseArrayCompat<View>()
    private var emptyClickListener: View.OnClickListener? = null
    private var btnText: String? = null
    private var tvText: String? = null


    private fun isHeaderViewPos(position: Int): Boolean {
        return position < mHeaders.size()
    }

    private fun isFooterViewPos(position: Int): Boolean {
        return position >= mHeaders.size() + getRealItemCount()
    }

    fun addHeaderView(view: View) {
        mHeaders.put(mHeaders.size() + BASE_ITEM_TYPE_HEADER, view)
    }

    fun addFooterView(view: View) {
        mFooters.put(mFooters.size() + BASE_ITEM_TYPE_FOOTER, view)
    }

    fun getHeadersCount(): Int = mHeaders.size()
    fun getFootersCount(): Int = mFooters.size()


    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        if (viewType == BASE_ITEM_TYPE_EMPTY) {
            val emptyView =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_empty_view, parent, false)
            return EmptyHolder(emptyView)
        }

        var itemView = mHeaders.get(viewType)
        if (itemView != null) {
            return BaseHolder(itemView)
        }
        itemView = mFooters.get(viewType)
        if (itemView != null) {
            return BaseHolder(itemView)
        }

        itemView = LayoutInflater.from(mContext)
            .inflate(mLayoutId, parent, false)
        return getRealViewHolder(itemView)
    }


    final override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }

        if (holder is BaseEmptyAdapter<*, *>.EmptyHolder) {
            holder.btn.setOnClickListener(emptyClickListener)
            if (!TextUtils.isEmpty(tvText)) {
                holder.tv.text = tvText
            }
            if (!TextUtils.isEmpty(btnText)) {
                holder.btn.text = btnText
            } else {
                holder.btn.visibility = View.INVISIBLE
            }
            extendEmptyHolder(holder, position)
        } else {
            bindRealHolder(holder as VH, position - getHeadersCount())
        }

    }


    abstract fun bindRealHolder(holder: VH, position: Int)
    protected fun extendEmptyHolder(holder: BaseEmptyAdapter<*, *>.EmptyHolder, position: Int) {}


    final override fun getItemCount(): Int {
        val empty = if (items.size == 0) {
            1
        } else {
            items.size
        }

        return mHeaders.size() + mFooters.size() + empty
    }

    fun getRealItemCount(): Int {
        return items.size
    }


    final override fun getItemViewType(position: Int): Int {
        if (items.size == 0) {
            return BASE_ITEM_TYPE_EMPTY
        }

        if (isHeaderViewPos(position)) {
            return mHeaders.keyAt(position)
        }
        if (isFooterViewPos(position)) {
            return mFooters.keyAt(position - mHeaders.size() - items.size)
        }

        return getRealItemViewType(position - mHeaders.size())

    }

    /**
     * NOTE:don't conflict with
     * @see #BASE_ITEM_TYPE_HEADER
     * and @see #BASE_ITEM_TYPE_FOOTER
     * and @see #BASE_ITEM_TYPE_EMPTY
     */
    fun getRealItemViewType(position: Int): Int = 0

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
}