package com.v.oldcall.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/12/7
 */
class AlphabetLayout : LinearLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private lateinit var mAlphabetView: AlphabetView
    private lateinit var mHintView: TextView
    private var listener: OnLetterChooseListener? = null

    private fun init() {
        orientation = HORIZONTAL
        val res = context.resources
        val dp30 = res.getDimension(R.dimen.space_30).toInt()

        val hParams = LayoutParams(
            dp30 * 2,
            dp30 * 2
        )
        mHintView = TextView(context)
        mHintView.gravity = Gravity.CENTER
        mHintView.visibility = View.INVISIBLE
        mHintView.setTextColor(res.getColor(R.color.orange_3))
        mHintView.textSize = 24f
        mHintView.setBackgroundColor(res.getColor(R.color.teal_7))
        hParams.topMargin = dp30 * 4
        mHintView.layoutParams = hParams


        val aParam = LayoutParams(
            dp30,
            LayoutParams.MATCH_PARENT
        )
        mAlphabetView = AlphabetView(context)
        mAlphabetView.layoutParams = aParam


        addView(mHintView)
        addView(mAlphabetView)

        setListeners()
    }

    private fun setListeners() {
        mAlphabetView.setOnItemChooseListener(object : AlphabetView.OnItemChooseListener {
            override fun onItemChoose(word: String) {
                mHintView.visibility = View.VISIBLE
                mHintView.text = word
                listener?.onLetterChoose(word)
            }

            override fun onItemChosen() {
                mHintView.visibility = View.GONE
            }

        })

    }

    public fun updateCurrentLetter(letter: String?) {
        letter?.let { mAlphabetView.updateCurrentWord(it) }
    }

    public fun setOnLetterChooseListener(listener: OnLetterChooseListener) {
        this.listener = listener
    }

    public interface OnLetterChooseListener {
        fun onLetterChoose(letter: String)
    }


}