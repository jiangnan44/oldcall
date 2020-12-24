package com.v.oldcall.entities

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import com.v.oldcall.utils.PinYinStringUtil
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient

/**
 * Author:v
 * Time:2020/11/26
 */
@Entity
class ContactEntity() :Parcelable {
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

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        cid = parcel.readLong()
        phone = parcel.readString()
        avatar = parcel.readString()
        avatarColor = parcel.readInt()
        pinyin = parcel.readString()
        alpha = parcel.readString()
        jianpin = parcel.readString()
        isFrequent = parcel.readByte() != 0.toByte()
    }


    override fun toString(): String {
        return "ContactEntity:id=$id,avatar=${avatar},name=$name,phone=$phone"
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<ContactEntity> {
        override fun createFromParcel(parcel: Parcel): ContactEntity {
            return ContactEntity(parcel)
        }

        override fun newArray(size: Int): Array<ContactEntity?> {
            return arrayOfNulls(size)
        }
    }
}