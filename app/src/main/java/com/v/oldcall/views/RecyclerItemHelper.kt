package com.v.oldcall.views

import android.graphics.Canvas
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R
import com.v.oldcall.utils.CommonUtil
import kotlin.math.abs

/**
 * Author:v
 * Time:2020/12/21
 */
interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onItemDelete(position: Int)
}

class SlideItemHelperCallback : ItemTouchHelper.Callback {
    var mAdapter: ItemTouchHelperAdapter

    constructor(mAdapter: ItemTouchHelperAdapter) : super() {
        this.mAdapter = mAdapter
    }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mAdapter.onItemDelete(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false//can be open
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    val MAX_ICON_SIZE = 50f
    var fixedWidth = 0

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        with(viewHolder.itemView) {
            scrollX = 0
            findViewById<View>(R.id.ic_tv_del).also { it.visibility = View.VISIBLE }
            findViewById<View>(R.id.ic_iv_del).also {
                val ivWidth = getSlideLimitation(viewHolder) * 50 / 105
                val lp = it.layoutParams
                lp.width = ivWidth
                lp.height = ivWidth
                it.layoutParams = lp
                it.visibility = View.INVISIBLE
            }

        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (ItemTouchHelper.ACTION_STATE_SWIPE == actionState) {
            val absX = abs(dX)
            val slideLimit = getSlideLimitation(viewHolder)
            if (absX <= slideLimit) {
                viewHolder.itemView.scrollTo(-dX.toInt(), 0)
            } else if (absX <= recyclerView.width / 2) {
                val distance = recyclerView.width / 2 - slideLimit
                val factor = MAX_ICON_SIZE / distance
                var diff = (absX - slideLimit) * factor

                if (diff > MAX_ICON_SIZE) diff = MAX_ICON_SIZE

                with(viewHolder.itemView) {
                    findViewById<View>(R.id.ic_tv_del).also {
                        it.visibility = View.INVISIBLE
                    }
                    findViewById<View>(R.id.ic_iv_del).also {
                        val lp = it.layoutParams
                        lp.width = (fixedWidth + diff).toInt()
                        lp.height = lp.width
                        it.layoutParams = lp
                        it.visibility = View.VISIBLE
                    }
                }
            }


        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

    private fun getSlideLimitation(holder: RecyclerView.ViewHolder): Int {
        if (fixedWidth == 0) {
            fixedWidth = CommonUtil.dp2px(holder.itemView.context, 105f).toInt()
        }
        return fixedWidth
    }

}

