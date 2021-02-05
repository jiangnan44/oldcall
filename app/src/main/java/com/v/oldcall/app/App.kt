package com.v.oldcall.app

import com.v.oldcall.utils.ObjectBoxHelper
import kotlin.properties.Delegates

/**
 * Author:v
 * Time:2021/1/8
 */
class App : BaseApplication() {

    companion object {
        private var instance: App by Delegates.notNull()
        fun instance() = instance
        var needRefreshFrequentContacts = false
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