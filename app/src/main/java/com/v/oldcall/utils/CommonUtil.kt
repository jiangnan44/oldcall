package com.v.oldcall.utils

import android.content.Context
import android.util.TypedValue
import com.v.oldcall.R
import kotlin.random.Random

/**
 * Author:v
 * Time:2020/12/2
 */
class CommonUtil {
    companion object {
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