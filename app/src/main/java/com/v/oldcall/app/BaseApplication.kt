package com.v.oldcall.app

import android.app.Application
import com.v.oldcall.BuildConfig
import com.v.oldcall.entities.MyObjectBox
import com.v.oldcall.utils.ObjectBoxHelper
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

/**
 * Author:v
 * Time:2020/11/25
 */
class BaseApplication : Application() {
    val TAG = "BaseApplication"


    override fun onCreate() {
        super.onCreate()
        initObjectBox()
    }

    private fun initObjectBox() {
        ObjectBoxHelper.init(this)
    }


}