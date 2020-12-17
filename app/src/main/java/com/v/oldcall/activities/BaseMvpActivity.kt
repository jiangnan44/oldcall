package com.v.oldcall.activities

import android.os.Bundle
import com.v.oldcall.mvps.BaseContract

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