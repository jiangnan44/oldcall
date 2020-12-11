package com.v.oldcall.entities

import android.net.Uri
import com.v.oldcall.utils.PinYinStringUtil

/**
 * Author:v
 * Time:2020/11/26
 */
class ContactEntity {
    var id: Long? = 0
    var avatar: Uri? = null
    var phone: String? = null
    var pinyin: String? = null
    var alpha: String? = null
    var jianpin: String? = null
    var name: String? = null
        set(value) {
            field = value
            pinyin = PinYinStringUtil.getPinYin(value)
            alpha = PinYinStringUtil.getHanZiAlphaLetter(value)
            jianpin = PinYinStringUtil.getPinYinHeadChar(value)
        }


    override fun toString(): String {
        return "ContactEntity:id=$id,avatar=${avatar?.toString()},name=$name,phone=$phone"
    }
}