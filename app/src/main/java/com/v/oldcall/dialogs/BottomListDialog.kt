package com.v.oldcall.dialogs

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.v.oldcall.R
import com.v.oldcall.adapters.BaseAdapter
import com.v.oldcall.adapters.BaseHolder
import com.v.oldcall.views.DividerDecoration

/**
 * Author:v
 * Time:2020/12/29
 */
class BottomListDialog {

    private var mDialog: BottomSheetDialog? = null
    private var mAdapter: ListBottomAdapter? = null

    constructor(context: Context) {
        mDialog = BottomSheetDialog(
            context
        ).also {
            it.setContentView(R.layout.dialog_bottom_list)
        }
        initView(context)
    }

    private fun initView(context: Context) {
        mAdapter = ListBottomAdapter(context)
        val rv = mDialog!!.findViewById<RecyclerView>(R.id.dbl_rv)
        rv!!.let {
            val llm = LinearLayoutManager(context)
            llm.orientation = LinearLayoutManager.VERTICAL
            it.layoutManager = llm
            it.addItemDecoration(DividerDecoration(context))
            it.adapter = mAdapter
        }

    }


    fun setListItems(list: List<SheetItem>) {
        mAdapter?.addData(list)
    }

    fun show() {
        mDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }


    fun dismiss() {
        mDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }


    data class SheetItem(var text: String, var action: Byte)

    inner class ListBottomAdapter : BaseAdapter<SheetItem, ViewHolder> {
        constructor(mContext: Context) : super(mContext, R.layout.item_bottom_list)


        override fun getRealViewHolder(itemView: View): ViewHolder {
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            items[position]?.let {


                holder.tvTitle.text = it.text
                holder.itemView.setOnClickListener { _ ->
                    listener?.onItemClick(it.action)
                    dismiss()
                }
            }
        }

    }

    class ViewHolder(itemView: View) : BaseHolder(itemView) {
        var tvTitle: TextView = itemView as TextView
    }

    private var listener: OnListItemClickListener? = null
    fun setListItemClickListener(listener: OnListItemClickListener) {
        this.listener = listener
    }

    interface OnListItemClickListener {
        fun onItemClick(action: Byte)
    }
}