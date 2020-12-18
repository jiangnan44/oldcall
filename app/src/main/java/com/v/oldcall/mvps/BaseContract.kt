package com.v.oldcall.mvps

import kotlinx.coroutines.*
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext

/**
 * Author:v
 * Time:2020/11/26
 */
class BaseContract {
    interface BaseModel {
        fun destroy()
    }

    interface BaseView {
        fun showLoading()
        fun dismissLoading()
        fun onError(msg: String)
    }

    abstract class BasePresenter<V, M> {
        private val scope = CoroutineScope(Dispatchers.Main)

        fun startCoroutine(
            context: CoroutineContext = Dispatchers.Main,
            start: CoroutineStart = CoroutineStart.DEFAULT,
            block: suspend CoroutineScope.() -> Unit
        ): Job = scope.launch(
            context, start, block
        )

        var mViewRef: Reference<V>? = null
        var mModel: M? = null


        val mView: V?
            get() = mViewRef?.get()


        fun attach(view: V) {
            mViewRef = WeakReference<V>(view)
        }

        open fun detach() {
            scope.cancel()
            mViewRef?.let {
                if (it.get() != null) {
                    it.clear()
                }
                mViewRef = null
            }
        }

    }
}