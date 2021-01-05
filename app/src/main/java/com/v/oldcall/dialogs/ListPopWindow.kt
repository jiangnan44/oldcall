package com.v.oldcall.dialogs

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.PopupWindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R
import com.v.oldcall.adapters.BaseAdapter
import com.v.oldcall.adapters.BaseHolder
import com.v.oldcall.constants.Keys
import com.v.oldcall.utils.CommonUtil
import com.v.oldcall.views.DividerDecoration

/**
 * Author:v
 * Time:2020/12/22
 *
 */
class ListPopWindow : PopupWindow {
    private val TAG = "ListPopWindow"

    var mContext: Context
    var mAdapter: ListPopAdapter? = null
    var anchor: View? = null
    var mGravity = Gravity.TOP or Gravity.START
    var xOff = 0
    var yOff = 0


    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
        setupWindow()
    }

    private fun setupWindow() {
        elevation = 10f
        val w = CommonUtil.dp2px(mContext, 150f).toInt()
        width = w
        height = w
        isFocusable = true
        isOutsideTouchable = true
        PopupWindowCompat.setOverlapAnchor(this, true)
        setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.color.white))
    }

    private fun initView() {
        mAdapter = ListPopAdapter(mContext)
        with(RecyclerView(mContext)) {
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(mContext).also {
                it.orientation = LinearLayoutManager.VERTICAL
            }

            addItemDecoration(
                DividerDecoration(
                    LinearLayoutManager.VERTICAL,
                    ContextCompat.getColor(mContext, R.color.teal_3),
                    mContext
                ).apply { setDividerLength(1) }
            )
            contentView = this
            adapter = mAdapter
        }
    }

    fun setData(list: List<ListItem>) {
        mAdapter?.addData(list)
    }

    fun show() {
        if (contentView == null) {
            throw IllegalStateException("Empty contentView")
        }

        if (anchor == null) {
            throw IllegalStateException("Empty Anchor View")
        }

        showAsDropDown(anchor, xOff, yOff, mGravity)
    }


    data class ListItem(@DrawableRes var icon: Int = 0, var text: String, var action: Byte)

    inner class ListPopAdapter : BaseAdapter<ListItem, ViewHolder> {
        constructor(mContext: Context) : super(mContext, R.layout.item_pop_list)


        override fun getRealViewHolder(itemView: View): ViewHolder {
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            items[position]?.let {
                if (it.icon <= 0) {
                    holder.ivIcon.visibility = View.GONE
                } else {
                    holder.ivIcon.visibility = View.VISIBLE
                    holder.ivIcon.setImageResource(it.icon)
                }

                holder.tvTitle.text = it.text
                holder.itemView.setOnClickListener { _ ->
                    listener?.onItemClick(it.action)
                    dismiss()
                }
            }
        }

    }

    class ViewHolder(itemView: View) : BaseHolder(itemView) {
        var ivIcon: ImageView = itemView.findViewById(R.id.ipl_iv_icon)
        var tvTitle: TextView = itemView.findViewById(R.id.ipl_tv_title)
    }

    private var listener: OnListItemClickListener? = null
    fun setListItemClickListener(listener: OnListItemClickListener) {
        this.listener = listener
    }

    interface OnListItemClickListener {
        fun onItemClick(action: Byte)
    }
}