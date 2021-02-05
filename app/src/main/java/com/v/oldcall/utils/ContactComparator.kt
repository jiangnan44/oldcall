package com.v.oldcall.utils

import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2020/12/11
 */
class ContactComparator : Comparator<ContactEntity> {
    override fun compare(o1: ContactEntity, o2: ContactEntity): Int {
        return compareString(o1.name!!, o2.name!!)
    }

    private fun compareString(name1: String, name2: String): Int {
        if (name1.length == 0 && name2.length == 0) {
            return 0
        }
        if (name1.isEmpty()) {
            return -1
        }
        if (name2.isEmpty()) {
            return 1
        }

        val first1 = name1.first().toString()
        val first2 = name2.first().toString()
        val type1 = getCharType(first1)
        val type2 = getCharType(first2)

        if (type1 > type2) {
            return -1
        }
        if (type1 < type2) {
            return 1
        }

        if (type1 < 4) {//letter or number
            val ret = first1.compareTo(first2)
            return if (ret != 0) {
                ret
            } else {
                compareString(name1.substring(1), name2.substring(1))
            }
        }

        val retHanZi = compareHanZi(first1, first2)

        return if (retHanZi != 0) {
            retHanZi
        } else {
            compareString(name1.substring(1), name2.substring(1))
        }

    }

    private fun compareHanZi(z1: String, z2: String): Int {
        val py1 = PinYinStringUtil.getFirstPinYin(z1)
        val py2 = PinYinStringUtil.getFirstPinYin(z2)
        return py1.compareTo(py2)
    }

    private fun getCharType(src: String): Int {
        return when {
            PinYinStringUtil.isHanZi(src) -> 4
            PinYinStringUtil.isLetter(src) -> 3
            PinYinStringUtil.isNumber(src) -> 2
            else -> 0
        }
    }

}