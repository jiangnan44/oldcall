package com.v.oldcall.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import com.v.oldcall.R
import java.util.regex.Pattern
import kotlin.random.Random

/**
 * Author:v
 * Time:2020/12/2
 */
class CommonUtil {
    companion object {

        fun isFastClick(v: View): Boolean {
            val MIN_CLICK_TIME = 500L
            val obj = v.getTag(R.id.quick_click)
            val lastClickTime = if (obj == null) 0L else obj as Long
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime < MIN_CLICK_TIME) {
                return true
            }
            v.setTag(R.id.quick_click, curClickTime)
            return false
        }


        fun dp2px(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.resources.displayMetrics
            )
        }

        fun sp2px(context: Context, sp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                context.resources.displayMetrics
            )
        }


        fun isPhone(str: String): Boolean {
            return Pattern.compile("^1\\d{10}$").matcher(str).matches()
        }


        fun getRandomColor(): Int {
            val seed =
                intArrayOf(
                    R.color.blue_5,
                    R.color.indigo_5,
                    R.color.purple_5,
                    R.color.cyan_5,
                    R.color.green_5,
                    R.color.lime_5,
                    R.color.orange_5,
                    R.color.teal_5,
                    R.color.light_green_5,
                    R.color.deep_purple_5
                )

            return seed[Random.nextInt(seed.size)]
        }


    }


}