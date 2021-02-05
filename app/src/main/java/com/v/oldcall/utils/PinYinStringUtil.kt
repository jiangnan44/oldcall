package com.v.oldcall.utils

import android.util.ArrayMap
import com.github.promeg.pinyinhelper.Pinyin
import java.util.*
import java.util.regex.Pattern

/**
 * Author:v
 * Time:2020/12/11
 */
object PinYinStringUtil {
    //多音字替换为我们希望的同样发音的单音字，可以继续增加
    private val homophoneFirstName = ArrayMap<String, String>()

    init {
        homophoneFirstName["仇"] = "球"
        homophoneFirstName["项"] = "向"
        homophoneFirstName["单"] = "善"
        homophoneFirstName["区"] = "呕"
        homophoneFirstName["查"] = "渣"
        homophoneFirstName["解"] = "谢"
        homophoneFirstName["翟"] = "宅"
    }


    /**
     * 全拼 ： 兰丫头 --LANYATOU
     */
    fun getFullPinYin(src: String?): String {
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
        val srcArray = s.toCharArray()
        val sb = StringBuffer()
        for (c in srcArray) {
            sb.append(Pinyin.toPinyin(c))
        }
        return sb.toString().toUpperCase(Locale.getDefault())
    }



    /**
     * 首字拼音：王李三 --WANG
     */
    fun getFirstPinYin(src: String?): String {
        if (src == null) {
            return ""
        }

        var s = src.trim()
        if (s.isEmpty()) {
            return ""
        }

        val firstChar = s.first().toString()

        if (!Pinyin.isChinese(s.first())) {
            return firstChar.toUpperCase(Locale.getDefault())
        }

        s = if (homophoneFirstName.containsKey(firstChar)) {
            homophoneFirstName[firstChar]!!
        } else {
            firstChar
        }


        return Pinyin.toPinyin(s.first()).toUpperCase(Locale.getDefault())
    }


    /**
     * 简拼 如 王大三 --WDS
     */
    fun getPinYinHeadLetter(src: String?): String {
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
        val srcArray = s.toCharArray()
        val sb = StringBuffer()
        for (c in srcArray) {
            sb.append(Pinyin.toPinyin(c).first())
        }

        return sb.toString().toUpperCase(Locale.getDefault())
    }



    /**
     * 获得汉语拼音首字母 如 王大三 --W
     */
    fun getPinYinFirstLetter(src: String?): String {
        if (src == null) {
            return "#"
        }

        var s = src.trim()
        if (s.isEmpty()) {
            return "#"
        }

        s = s.first().toString()

        if (isLetter(s)) {
            return s.toUpperCase(Locale.getDefault())
        }

        if (homophoneFirstName.containsKey(s)) {
            s = s.replace(s, homophoneFirstName[s]!!, true)
        }


        if (!Pinyin.isChinese(s[0])) {
            return "#"
        }

        s = Pinyin.toPinyin(s[0])

        return s.first().toString().toUpperCase(Locale.getDefault())
    }



    fun isLetter(src: String): Boolean {
        return (Pattern.compile("^[A-Za-z]+$").matcher(src).matches())
    }

    fun isHanZi(src: String): Boolean {
        return (Pattern.compile("[\\u4E00-\\u9FA5]+").matcher(src).matches())
    }

    /**
     * @param str must be a char
     */
    fun isNumber(str: String): Boolean {
        return Pattern.compile("^[1-9]+$").matcher(str).matches()
    }
}