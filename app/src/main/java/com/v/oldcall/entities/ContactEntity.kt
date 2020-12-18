package com.v.oldcall.entities

import android.net.Uri
import com.v.oldcall.utils.PinYinStringUtil
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient

/**
 * Author:v
 * Time:2020/11/26
 */
@Entity
class ContactEntity {
    @Id
    var id: Long = 0
    var cid: Long = 0
    var phone: String? = null
    var avatar: String? = null

    @Transient
    var avatarColor: Int = 0
    var pinyin: String? = null
    var alpha: String? = null
    var jianpin: String? = null
    var isFrequent: Boolean = false
    var name: String? = null
        set(value) {
            field = value
            pinyin = pinyin ?: PinYinStringUtil.getPinYin(value)
            alpha = alpha ?: PinYinStringUtil.getHanZiAlphaLetter(value)
            jianpin = jianpin ?: PinYinStringUtil.getPinYinHeadChar(value)
        }


    override fun toString(): String {
        return "ContactEntity:id=$id,avatar=${avatar},name=$name,phone=$phone"
    }
}