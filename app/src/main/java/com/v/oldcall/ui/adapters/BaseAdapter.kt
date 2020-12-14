package com.v.oldcall.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
 * Author:v
 * Time:2020/12/1
 */
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseHolder> {
    protected var mContext: Context
    protected var mLayoutId: Int
    protected var items: MutableList<T?>

    constructor(mContext: Context, @LayoutRes layoutId: Int) : super() {
        this.mContext = mContext
        items = ArrayList()
        this.mLayoutId = layoutId
    }

    constructor(mContext: Context, items: MutableList<T?>?, @LayoutRes layoutId: Int) : super() {
        this.mContext = mContext
        this.items = items ?: ArrayList()
        this.mLayoutId = layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false)
        return getViewHolder(view)
    }


    abstract fun getViewHolder(itemView: View): BaseHolder

    override fun getItemCount(): Int = items.size


    fun getItem(position: Int): T {
        return items[position]!!
    }

    fun getDataList(): MutableList<T?> = items

    fun addData(items: List<T?>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addNewList(list: List<T?>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun setData(index: Int, data: T) {
        if (index >= items.size || index < 0) {
            return
        }
        items[index] = data
        notifyItemChanged(index)
    }

    fun addData(@IntRange(from = 0) position: Int, data: T) {
        items.add(position, data)
        notifyItemInserted(position)
        compatDataSizeChanged(1)
    }


    private fun compatDataSizeChanged(size: Int) {
        val dataSize = items?.size ?: 0
        if (dataSize == size) {
            notifyDataSetChanged()
        }
    }

    fun addData(@NonNull data: T) {
        items.add(data)
        notifyItemInserted(items.size)
    }

    fun remove(position: Int) {
        if (position >= items.size || position < 0) {
            return
        }
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size - position)
    }

    fun remove(item: T) {
        val position = items.indexOf(item)
        if (position == -1) return
        remove(position)
    }


}