package com.v.oldcall.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/12/7
 */
class AlphabetView : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    companion object {
        const val TAG = "AlphabetView"
        val mAlphabet = arrayOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z", "#"
        )
    }

    private var listener: OnItemChooseListener? = null
    private var mPaint: Paint = Paint()
    private var mSelectIndex = 0
    private var mTextSize: Float = 0f
    private var mTextColor = Color.BLACK
    private var mTextSizeChoose = 0f
    private var mTextColorChoose = Color.BLUE


    private fun init() {
        val res = context.resources
        mTextSize = res.getDimension(R.dimen.txt_14)
        mTextSizeChoose = mTextSize * 1.1f
        mTextColor = res.getColor(R.color.teal_3)
        mTextColorChoose = res.getColor(R.color.teal_5)
    }

    public fun setTextColor(color: Int) {
        mTextColor = color
    }

    public fun setChooseTextColor(color: Int) {
        mTextColorChoose = color
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paintText(canvas!!)
    }

    private fun paintText(canvas: Canvas) {
        mPaint.isAntiAlias = true
        mPaint.typeface = Typeface.DEFAULT_BOLD

        val size = mAlphabet.size
        val singleHeight = height / size


        for (i in 0 until size) {
            if (i == mSelectIndex) {
                mPaint.color = mTextColorChoose
                mPaint.textSize = mTextSizeChoose
            } else {
                mPaint.color = mTextColor
                mPaint.textSize = mTextSize
            }

            val x = width / 2f - mPaint.measureText(mAlphabet[i]) / 2f
            val y = (singleHeight * i + singleHeight).toFloat()
            canvas.drawText(mAlphabet[i], x, y, mPaint)

        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                val index = (event.y / height * mAlphabet.size).toInt()
                if (index > 0 && index < mAlphabet.size) {
                    listener?.onItemChoose(mAlphabet[index])
                    mSelectIndex = index
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                listener?.onItemChosen()
            }
        }

        return true
    }


    public fun updateCurrentWord(letter: String) {
        val index = mAlphabet.indexOf(letter)
        if (mSelectIndex == index || index < 0) {
            return
        }
        mSelectIndex = index
        invalidate()
    }


    public fun setOnItemChooseListener(listener: OnItemChooseListener) {
        this.listener = listener
    }

    public interface OnItemChooseListener {
        fun onItemChoose(word: String)
        fun onItemChosen()
    }

}