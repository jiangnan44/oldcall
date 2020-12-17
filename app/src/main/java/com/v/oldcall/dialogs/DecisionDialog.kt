package com.v.oldcall.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/11/25
 */
class DecisionDialog(builder: Builder) : Dialog(builder.mContext!!, builder.mThemeId),
    View.OnClickListener {

    companion object {
        private const val TAG = "DecisionDialog"
    }

    private var mContext: Context? = null
    private var title: String? = null
    private var content: String? = null
    private var leftText: String? = null
    private var rightText: String? = null
    private var leftListener: DialogClickListener? = null
    private var rightListener: DialogClickListener? = null


    init {
        mContext = builder.mContext
        title = builder.title
        content = builder.content
        leftText = builder.leftText
        rightText = builder.rightText
        leftListener = builder.leftListener
        rightListener = builder.rightListener
        setCancelable(builder.canCancel)
        setCanceledOnTouchOutside(builder.canCancelOutside)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_decision)
        initView()
    }

    private fun initView() {
        val tvTitle: TextView = findViewById(R.id.dd_tv_title)
        val tvContent: TextView = findViewById(R.id.dd_tv_content)
        val btnLeft: Button = findViewById(R.id.dd_btn_left)
        val btnRight: Button = findViewById(R.id.dd_btn_right)
        if (!TextUtils.isEmpty(title)) {
            tvTitle.text = title
        }
        if (!TextUtils.isEmpty(content)) {
            tvContent.text = content
        }
        if (!TextUtils.isEmpty(rightText)) {
            btnRight.text = rightText
        }
        if (!TextUtils.isEmpty(leftText)) {
            btnLeft.text = leftText
        }

        btnLeft.setOnClickListener(this)
        btnRight.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.dd_btn_right -> {
                dismiss()
                rightListener?.onClick(this)
            }

            R.id.dd_btn_left -> {
                dismiss()
                leftListener?.onClick(this)
            }
        }
    }

    override fun show() {
        if (isActivityDead() || isShowing) return
        super.show()
    }

    override fun dismiss() {
        if (isActivityDead() || !isShowing) return
        super.dismiss()
    }

    private fun isActivityDead(): Boolean {
        if (mContext == null) return true
        if (mContext is Activity) {
            if ((mContext as Activity).isFinishing || (mContext as Activity).isDestroyed) return true
        }
        return false
    }


    class Builder {
        internal var mThemeId: Int = 0
        internal var canCancel = true
        internal var canCancelOutside = true
        internal var mContext: Context? = null
        internal var title: String? = null
        internal var content: String? = null
        internal var leftText: String? = null
        internal var rightText: String? = null
        internal var leftListener: DialogClickListener? = null
        internal var rightListener: DialogClickListener? = null

        constructor(context: Context) {
            mContext = context
        }

        constructor(mContext: Context, mThemeId: Int) {
            this.mThemeId = mThemeId
            this.mContext = mContext
        }

        fun withTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun withContent(content: String): Builder {
            this.content = content
            return this
        }

        fun withLeftText(txt: String): Builder {
            this.leftText = txt
            return this
        }

        fun withRightText(txt: String): Builder {
            this.rightText = txt
            return this
        }

        fun withLeftListener(listener: DialogClickListener): Builder {
            leftListener = listener
            return this
        }

        fun withRightListener(listener: DialogClickListener): Builder {
            rightListener = listener
            return this
        }

        fun withCancelOnBack(cancel: Boolean): Builder {
            canCancel = cancel
            return this
        }

        fun withCancelOutside(cancelOutside: Boolean): Builder {
            this.canCancelOutside = cancelOutside
            return this
        }

        fun build() = DecisionDialog(this)


    }

    public interface DialogClickListener {
        fun onClick(dialog: Dialog)
    }


}