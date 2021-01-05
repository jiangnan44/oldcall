package com.v.oldcall.utils

import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination
import java.io.FileNotFoundException
import java.lang.Exception
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

    /**
     * 全拼 ： 兰丫头 --LANYATOU
     */
    fun getPinYin(src: String?): String {
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
        var pyArray = arrayOfNulls<String>(srcArray.size)
        val pyFormat = HanyuPinyinOutputFormat()
        pyFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        pyFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        pyFormat.vCharType = HanyuPinyinVCharType.WITH_V

        val sb = StringBuffer()
        for (c in srcArray) {
            if (c.toString().matches("[\\u4E00-\\u9FA5]+".toRegex())) {
                try {
                    pyArray = PinyinHelper.toHanyuPinyinStringArray(c, pyFormat)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    pyArray[0] = ""
                }catch (e2:Exception){
                    e2.printStackTrace()
                    pyArray[0] = ""
                }
                sb.append(pyArray[0])
            } else {
                sb.append(c)
            }
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

        if (!isHanZi(firstChar)) {
            return firstChar.toUpperCase(Locale.getDefault())
        }

        s = if (homophoneFirstName.containsKey(firstChar)) {
            homophoneFirstName[firstChar]!!
        } else {
            firstChar
        }

        val srcArray = s.toCharArray()
        var pyArray = arrayOfNulls<String>(srcArray.size)
        val pyFormat = HanyuPinyinOutputFormat()
        pyFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        pyFormat.toneType = HanyuPinyinToneType.WITHOUT_TONE
        pyFormat.vCharType = HanyuPinyinVCharType.WITH_V
        val ret = StringBuffer()

        for (c in srcArray) {
            if (c.toString().matches("[\\u4E00-\\u9FA5]+".toRegex())) {
                try {
                    pyArray = PinyinHelper.toHanyuPinyinStringArray(c, pyFormat)
                } catch (e: BadHanyuPinyinOutputFormatCombination) {
                    e.printStackTrace()
                }
                ret.append(pyArray[0])
            } else {
                ret.append(c)
            }
        }

        return ret.toString().toUpperCase(Locale.getDefault())
    }

    /**
     * 简拼 如 王大三 --WDS
     */
    fun getPinYinHeadChar(src: String?): String {
        if (src == null || src.trim().isEmpty()) {
            return ""
        }

        val ret = StringBuilder()
        for (c in src) {
            val pyArray = PinyinHelper.toHanyuPinyinStringArray(c)
            if (null != pyArray) {
                ret.append(pyArray[0][0])
            } else {
                ret.append(c)
            }
        }

        return ret.toString().toUpperCase(Locale.getDefault())
    }

    /**
     * 获得汉语拼音首字母 如 王大三 --W
     */
    fun getHanZiAlphaLetter(src: String?): String {
        if (src == null) {
            return "#"
        }

        var s = src.trim()
        if (s.isEmpty()) {
            return "#"
        }

        val c = s.first().toString()
        if (!isHanZi(c) && !isLetter(c)) {
            return "#"
        }

        if (isLetter(c)) {
            return c.toUpperCase(Locale.getDefault())
        } else {
            val headChars = getHeadChar(src)
            if (!TextUtils.isEmpty(headChars)) {
                return headChars.first().toString().toUpperCase(Locale.getDefault())
            }
        }
        return "#"
    }

    private fun getHeadChar(src: String): String {

        var s = src.trim()
        if (s.isEmpty()) {
            return ""
        }
        val firstChar = s.first().toString()
        if (homophoneFirstName.containsKey(firstChar)) {
            s = homophoneFirstName[firstChar]!!
        }
        val c = s.first()
        val pyArray = PinyinHelper.toHanyuPinyinStringArray(c)
        val ret = if (pyArray != null) {
            pyArray[0][0]
        } else {
            c
        }
        return ret.toString().toUpperCase(Locale.getDefault())
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