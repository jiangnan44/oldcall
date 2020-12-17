package com.v.oldcall.utils

import android.content.Context
import com.v.oldcall.entities.MyObjectBox
import io.objectbox.BoxStore

/**
 * Author:v
 * Time:2020/12/17
 */
object ObjectBoxHelper {
    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }
}