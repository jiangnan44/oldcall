package com.v.oldcall.utils

import android.util.ArrayMap
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import kotlin.collections.containsKey as containsKey1

/**
 * Author:v
 * Time:2020/12/11
 */
object PinYinStringUtil {
    //多音字替换为我们希望的同样发音的单音字，可以继续增加
    private val homophoneFirstName = ArrayMap<String, String>()

    init {
        homophoneFirstName["姜"] = "江"
        homophoneFirstName["阎"] = "严"
        homophoneFirstName["萧"] = "肖"
        homophoneFirstName["陆"] = "路"
        homophoneFirstName["伍"] = "武"
        homophoneFirstName["解"] = "谢"
        homophoneFirstName["余"] = "于"
        homophoneFirstName["傅"] = "付"
        homophoneFirstName["章"] = "张"
        homophoneFirstName["徐"] = "许"
        homophoneFirstName["戴"] = "代"
        homophoneFirstName["魏"] = "卫"
        homophoneFirstName["兰"] = "蓝"
        homophoneFirstName["席"] = "习"
        homophoneFirstName["向"] = "项"
    }

    public fun getPinYin(src: String?): String {
        if (src == null) {
            return ""
        }

        var s = src.trim()
        if (s.isEmpty()) {
            return ""
        }

        val firstChar = s.first().toString()
        if (homophoneFirstName.containsKey(firstChar)) {
            s = s.replace(firstChar, homophoneFirstName[firstChar]!!, true)
        }
        val t1 = s.toCharArray()
        val t2 = arrayOfNulls<String>(t1.size)
        val pyFormat= HanyuPinyinOutputFormat()
        return ""
    }

}