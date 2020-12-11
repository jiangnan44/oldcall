package com.v.oldcall.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.utils.CommonUtil
import kotlin.math.roundToInt

/**
 * Author:v
 * Time:2020/12/10
 */
class DividerDecoration : RecyclerView.ItemDecoration {
    private val mBounds = Rect()
    private var mPaint: Paint? = null
    private var mDivider: Drawable? = null
    private var dividerLength: Int = 0
    private var orientation = LinearLayoutManager.VERTICAL


    /**
     * VERTICAL
     * translucent divider with 1dp height
     */
    constructor(context: Context) {
        dividerLength = CommonUtil.dp2px(context, 1f).toInt()
    }


    /**
     * @param orientation either be LinearLayoutManager.VERTICAL
     * or LinearLayoutManager.HORIZONTAL,default is vertical
     */
    constructor(context: Context, orientation: Int) {
        this.orientation = orientation
        if (orientation != LinearLayoutManager.HORIZONTAL) {
            this.orientation = LinearLayoutManager.VERTICAL
        }
        if (0 >= dividerLength) {
            dividerLength = CommonUtil.dp2px(context, 1f).toInt()
        }
    }

    constructor(context: Context, orientation: Int, @DrawableRes dividerId: Int) : this(
        context,
        orientation
    ) {
        mDivider = ContextCompat.getDrawable(context, dividerId)
            ?: throw IllegalArgumentException("did not get divider drawable $dividerId")

        dividerLength = with(mDivider) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                this!!.intrinsicHeight
            } else {
                this!!.intrinsicWidth
            }
        }
    }


    constructor(orientation: Int, dividerColor: Int, context: Context) : this(
        context,
        orientation
    ) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = dividerColor
        mPaint!!.style = Paint.Style.FILL
    }

    public fun setDividerLength(dividerLength: Int) {
        this.dividerLength = dividerLength
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, dividerLength)
        } else {
            outRect.set(0, 0, dividerLength, 0)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int

        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = child.translationX.roundToInt() + mBounds.right
            val left = right - dividerLength

            mDivider?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }

            mPaint?.let {
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    it
                )
            }
        }
        canvas.restore()
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop,
                right, parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }


        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - dividerLength

            mDivider?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(canvas)
            }

            mPaint?.let {
                canvas.drawRect(
                    left.toFloat(),
                    top.toFloat(),
                    right.toFloat(),
                    bottom.toFloat(),
                    it
                )
            }
        }
        canvas.restore()
    }
}