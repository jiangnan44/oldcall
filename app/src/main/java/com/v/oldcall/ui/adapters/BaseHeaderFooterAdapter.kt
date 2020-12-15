package com.v.oldcall.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Author:v
 * Time:2020/12/15
 */
abstract class BaseHeaderFooterAdapter<T, VH : BaseHolder> : BaseAdapter<T> {
    private val BASE_ITEM_TYPE_HEADER = 100_000
    private val BASE_ITEM_TYPE_FOOTER = 200_000
    private val mHeaders = SparseArrayCompat<View>()
    private val mFooters = SparseArrayCompat<View>()

    constructor(mContext: Context, layoutId: Int) : super(mContext, layoutId)
    constructor(mContext: Context, items: MutableList<T?>?, layoutId: Int) : super(
        mContext,
        items,
        layoutId
    )


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


    final override fun getItemViewType(position: Int): Int {
        if (isHeaderViewPos(position)) {
            return mHeaders.keyAt(position)
        }
        if (isFooterViewPos(position)) {
            return mFooters.keyAt(position - mHeaders.size() - items.size)
        }

        return getRealItemViewType(position - mHeaders.size())
    }

    /**
     * NOTE:don't conflict with @see #BASE_ITEM_TYPE_HEADER
     * and @see #BASE_ITEM_TYPE_FOOTER
     */
    fun getRealItemViewType(position: Int): Int = 0


    override fun getItemCount(): Int {
        return mHeaders.size() + mFooters.size() + items.size
    }

    fun getRealItemCount(): Int {
        return items.size
    }

    final override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (isHeaderViewPos(position)) {
            return
        }
        if (isFooterViewPos(position)) {
            return
        }

        bindRealHolder(holder as VH, position - getHeadersCount())
    }

    abstract fun bindRealHolder(holder: VH, position: Int)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        var view = mHeaders.get(viewType)
        if (null != view) {
            return BaseHolder(view)
        }
        view = mFooters.get(viewType)
        if (null != view) {
            return BaseHolder(view)
        }

        view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false)
        return getRealViewHolder(view)
    }


}