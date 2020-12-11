package com.v.oldcall.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.v.oldcall.R

/**
 * Author:v
 * Time:2020/11/25
 */
class LoadingDialog : Dialog {

    private var text: String? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        initView()
    }

    private fun initView() {
        val tv = findViewById<TextView>(R.id.tv_loading)
        if (!TextUtils.isEmpty(text)) {
            tv.text = text
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun dismiss() {
        if (context == null || !isShowing) return
        super.dismiss()
    }

    fun setText(text: String?) {
        this.text = text
    }

    companion object {
        private const val TAG = "LoadingDialog"
    }
}