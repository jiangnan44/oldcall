package com.v.oldcall.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.v.oldcall.R
import kotlin.math.abs

/**
 * Author:v
 * Time:2020/12/23
 * original by https://blog.csdn.net/dapangzao/article/details/80524774
 */
class SwipeRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mScroller = Scroller(context)
    }

    companion object {
        private const val TAG = "SwipeRecyclerView"
        private const val INVALID_POSITION = -1
        private const val INVALID_CHILD_WIDTH = -1
        private const val SWIPE_VELOCITY = 600
    }

    private var mFlingView: ViewGroup? = null
    private var mVelocityTracker: VelocityTracker? = null
    private var mTouchFrame: Rect? = null
    private var mScroller: Scroller
    private var mTouchSlop = 0
    private var mLastX = 0f
    private var mFirstX = 0f
    private var mFirstY = 0f
    private var mPosition = 0
    private var mMenuViewWidth = 0
    private var isSlide = false


    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mFlingView?.scrollTo(mScroller.currX, mScroller.currY)
            postInvalidate()
        }
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (null == e) {
            return super.onInterceptTouchEvent(e)
        }

        val x = e.x
        val y = e.y
        obtainVelocity(e)

        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }

                mFirstX = x
                mFirstY = y
                mLastX = x

                mPosition = pointToPosition(x.toInt(), y.toInt())
                if (mPosition != INVALID_POSITION) {
                    val firstPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    mPosition += firstPosition

                    val view = mFlingView

                    mFlingView = getChildAt(mPosition - firstPosition) as ViewGroup

                    if (view != null && mFlingView != view && view.scrollX != 0) {
                        animCloseMenu(view)//slide back the opened item
                    }

                    //NOTE!! define a view/viewGroup @+id/srv_slide_menu in your itemView
                    val menuView = mFlingView?.findViewById<View>(R.id.srv_slide_menu)
                    mMenuViewWidth = menuView?.width ?: INVALID_CHILD_WIDTH
                }
            }

            MotionEvent.ACTION_MOVE -> {
                mVelocityTracker?.let {
                    it.computeCurrentVelocity(1000)
                    val xVelocity = it.xVelocity
                    val yVelocity = it.yVelocity
                    val mX = abs(x - mFirstX)
                    if (abs(xVelocity) > SWIPE_VELOCITY && abs(xVelocity) > abs(yVelocity) ||
                        mX >= mTouchSlop && mX > abs(y - mFirstY)
                    ) {
                        isSlide = true
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                releaseVelocity()
            }
        }

        return super.onInterceptTouchEvent(e)
    }

    private fun animCloseMenu(view: View) {
        if (view.scrollX == 0) return
        with(ValueAnimator()) {
            setFloatValues(view.scrollX.toFloat(), 0f)
            duration = 300L
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener { animator ->
                val value = animator.animatedValue as Float
                view.scrollTo(value.toInt(), 0)
            }
            start()
        }
    }


    override fun onTouchEvent(e: MotionEvent?): Boolean {
        if (isSlide && mPosition != INVALID_POSITION) {
            handleMenu(e!!)
            return true
        } else {
            closeMenu()
            releaseVelocity()
            return super.onTouchEvent(e)
        }
    }


    private fun handleMenu(e: MotionEvent) {
        val x = e.x
        obtainVelocity(e)
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                if (INVALID_CHILD_WIDTH != mMenuViewWidth) {
                    mFlingView?.let {
                        val dx = mLastX - x
                        if (it.scrollX + dx <= mMenuViewWidth
                            && it.scrollX + dx > 0
                        ) {
                            it.scrollBy(dx.toInt(), 0)
                        }
                        mLastX = x
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (INVALID_CHILD_WIDTH != mMenuViewWidth) {
                    mVelocityTracker?.computeCurrentVelocity(1000)
                    val scrollX = mFlingView?.scrollX!!.toInt()
                    val xV = mVelocityTracker?.xVelocity!!.toInt()
                    when {
                        (xV < -SWIPE_VELOCITY) -> {
                            mScroller.startScroll(
                                scrollX,
                                0,
                                mMenuViewWidth - scrollX,
                                0,
                                abs(mMenuViewWidth - scrollX)
                            )
                        }

                        (xV >= SWIPE_VELOCITY) -> {
                            mScroller.startScroll(scrollX, 0, -scrollX, 0)
                        }

                        (scrollX >= mMenuViewWidth / 2) -> {
                            mScroller.startScroll(
                                scrollX,
                                0,
                                mMenuViewWidth - scrollX,
                                0,
                                abs(mMenuViewWidth - scrollX)
                            )
                        }

                        else -> {
                            mScroller.startScroll(scrollX, 0, -scrollX, 0)
                        }
                    }
                    invalidate()
                }
                mMenuViewWidth = INVALID_CHILD_WIDTH
                isSlide = false
                mPosition = INVALID_POSITION
                releaseVelocity()
            }

            MotionEvent.ACTION_DOWN -> {
                //nothing happened
            }
        }

    }

    fun closeMenu() {
        mFlingView?.let {
            animCloseMenu(it)
        }
    }

    /**
     * calculate position with point(x,y)
     */
    private fun pointToPosition(x: Int, y: Int): Int {
        if (layoutManager !is LinearLayoutManager) {
            throw IllegalAccessException("Only support LinearLayoutManager")
        }

        if (null == mTouchFrame) {
            mTouchFrame = Rect()
        }
        val frame = mTouchFrame!!

        val count = childCount
        for (i in count - 1 downTo 0) {
            val child = getChildAt(i)
            if (View.VISIBLE == child.visibility) {
                child.getHitRect(frame)
                if (frame.contains(x, y)) {
                    return i
                }
            }
        }

        return INVALID_POSITION
    }

    private fun releaseVelocity() {
        mVelocityTracker?.let {
            it.clear()
            it.recycle()
            mVelocityTracker = null
        }
    }


    private fun obtainVelocity(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
    }
}