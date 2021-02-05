package com.v.oldcall.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.v.oldcall.utils.ObjectBoxHelper
import java.util.*
import kotlin.properties.Delegates

/**
 * Author:v
 * Time:2020/11/25
 */
open class BaseApplication : Application() {
    val TAG = "BaseApplication"
    internal val mActivityList: LinkedList<Activity> = LinkedList()


    override fun onCreate() {
        super.onCreate()
        registerActivityMonitor()
    }

    fun exitApp() {
        for (activity in mActivityList.reversed()) {
            if (activity != null && !activity.isFinishing) {
                mActivityList.remove(activity)
                activity.finish()
            }
        }
    }

    private fun registerActivityMonitor() {
        registerActivityLifecycleCallbacks(AppActivityLifecycleManager())
    }

    inner class AppActivityLifecycleManager : ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            mActivityList.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            //nothing to do
        }

        override fun onActivityResumed(activity: Activity) {
            //nothing to do
        }

        override fun onActivityPaused(activity: Activity) {
            //nothing to do
        }

        override fun onActivityStopped(activity: Activity) {
            //nothing to do
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            //nothing to do
        }

        override fun onActivityDestroyed(activity: Activity) {
            //nothing to do
            mActivityList.remove(activity)
        }

    }

}