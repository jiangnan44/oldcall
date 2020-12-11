package com.v.oldcall.entities

import android.net.Uri

/**
 * Author:v
 * Time:2020/11/26
 */
class ContactEntity {
    var id: Long? = 0
    var avatar: Uri? = null
    var name: String? = null
    var phone: String? = null


    override fun toString(): String {
        return "ContactEntity:id=$id,avatar=${avatar?.toString()},name=$name,phone=$phone"
    }
}