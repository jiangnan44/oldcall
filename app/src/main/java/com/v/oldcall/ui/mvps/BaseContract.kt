package com.v.oldcall.ui.mvps

import java.lang.ref.Reference
import java.lang.ref.WeakReference

/**
 * Author:v
 * Time:2020/11/26
 */
class BaseContract {
    interface BaseModel {}
    interface BaseView {
        fun showLoading()
        fun dismissLoading()
        fun onError(msg: String)
    }

    abstract class BasePresenter<V, M> {
        var mViewRef: Reference<V>? = null
        var mModel: M? = null


        val mView: V?
            get() = mViewRef?.get()


        fun attach(view: V) {
            mViewRef = WeakReference<V>(view)
        }

        open fun detach() {
            mViewRef?.let {
                if (it.get() != null) {
                    it.clear()
                }
                mViewRef = null
            }
        }

    }
}