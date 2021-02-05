package com.v.oldcall.entities

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
class ContactEntity() : Parcelable {
    @Id
    var id: Long = 0
    var cid: Long = 0//contact id
    var pid: Long = 0//photo id
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
            pinyin = pinyin ?: PinYinStringUtil.getFullPinYin(value)
            alpha = alpha ?: PinYinStringUtil.getPinYinFirstLetter(value)
            jianpin = jianpin ?: PinYinStringUtil.getPinYinHeadLetter(value)
        }

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        cid = parcel.readLong()
        pid = parcel.readLong()
        phone = parcel.readString()
        avatar = parcel.readString()
        avatarColor = parcel.readInt()
        pinyin = parcel.readString()
        alpha = parcel.readString()
        jianpin = parcel.readString()
        isFrequent = parcel.readByte() != 0.toByte()
        name = parcel.readString()
    }


    override fun toString(): String {
        return "ContactEntity:id=$id,cid=$cid,pid=$pid,avatar=${avatar},name=$name,phone=$phone"
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeLong(cid)
        dest.writeLong(pid)
        dest.writeString(phone)
        dest.writeString(avatar)
        dest.writeInt(avatarColor)
        dest.writeString(pinyin)
        dest.writeString(alpha)
        dest.writeString(jianpin)
        dest.writeByte(if (isFrequent) 1 else 0)
        dest.writeString(name)
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