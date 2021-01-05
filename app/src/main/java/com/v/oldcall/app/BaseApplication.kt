package com.v.oldcall.app

import android.app.Application
import com.v.oldcall.utils.ObjectBoxHelper
import kotlin.properties.Delegates

/**
 * Author:v
 * Time:2020/11/25
 */
class BaseApplication : Application() {
    val TAG = "BaseApplication"

    companion object {
        private var instance: BaseApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initObjectBox()
    }

    private fun initObjectBox() {
        ObjectBoxHelper.init(this)
    }


}