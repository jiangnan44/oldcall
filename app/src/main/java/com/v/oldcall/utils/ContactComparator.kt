package com.v.oldcall.utils

import com.v.oldcall.entities.ContactEntity
import java.util.regex.Pattern

/**
 * Author:v
 * Time:2020/12/11
 */
class ContactComparator : Comparator<ContactEntity> {
    override fun compare(o1: ContactEntity, o2: ContactEntity): Int {
        return compareString(o1.name, o2.name)
    }

    private fun compareString(name1: String?, name2: String?): Int {


        return 0
    }

    private fun isNumber(str: String): Boolean {
        val c = str.first().toString()
        return Pattern.compile("^[1-9]+$").matcher(c).matches()
    }
}