package com.v.oldcall.ui.activities

import android.os.Bundle
import android.os.PersistableBundle
import com.v.oldcall.ui.mvps.BaseContract

/**
 * Author:v
 * Time:2020/11/26
 */
abstract class BaseMvpActivity<P : BaseContract.BasePresenter<*, *>> :
    BaseActivity() {
    protected var mPresenter: P? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        initPresenter()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        mPresenter?.let {
            it.detach()
            mPresenter = null
        }
        super.onDestroy()

    }

    abstract fun initPresenter()


}