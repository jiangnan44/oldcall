package com.v.oldcall.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class ToastManager private constructor(context: Context) {
    private var mToast: Toast? = null


    init {
        if (null == mToast) {
            mToast = Toast.makeText(context.applicationContext, "", Toast.LENGTH_LONG)
        }
    }


    fun showLong(text: String?) {
        mToast?.let {
            it.setText(text)
            it.duration = Toast.LENGTH_LONG
            it.show()
        }
    }

    fun showLong(textID: Int) {
        mToast?.let {
            it.setText(textID)
            it.duration = Toast.LENGTH_LONG
            it.show()
        }
    }

    fun showShort(text: String?) {
        mToast?.let {
            it.setText(text)
            it.duration = Toast.LENGTH_SHORT
            it.show()
        }
    }

    fun showShort(textID: Int) {
        mToast?.let {
            it.setText(textID)
            it.duration = Toast.LENGTH_SHORT
            it.show()
        }
    }

    fun cancel() {
        mToast?.cancel()
    }

    fun setGravityCenter() {
        mToast?.setGravity(Gravity.CENTER, 0, 0)
    }

    companion object {
        private var mToastManager: ToastManager? = null

        fun getInstance(context: Context): ToastManager {
            if (null == mToastManager) {
                mToastManager = ToastManager(context.applicationContext)
            }
            return mToastManager!!
        }

        fun showLong(context: Context?, text: String?) {
            Toast.makeText(context?.applicationContext, text, Toast.LENGTH_LONG).show()
        }

        fun showLong(context: Context?, textID: Int) {
            Toast.makeText(context?.applicationContext, textID, Toast.LENGTH_LONG).show()
        }

        fun showShort(context: Context?, text: String?) {
            Toast.makeText(context?.applicationContext, text, Toast.LENGTH_SHORT).show()
        }

        fun showShort(context: Context?, textID: Int) {
            Toast.makeText(context?.applicationContext, textID, Toast.LENGTH_SHORT).show()
        }
    }

}