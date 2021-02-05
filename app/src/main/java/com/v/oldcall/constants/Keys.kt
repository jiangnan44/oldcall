package com.v.oldcall.constants

/**
 * Author:v
 * Time:2020/12/22
 */
class Keys {
    companion object {
        const val POP_ACTION_DIAL: Byte = 0x1
        const val POP_ACTION_MODIFY: Byte = 0x2
        const val POP_ACTION_REMOVE: Byte = 0x3

        const val BOTTOM_SHEET_TAKE_PHOTO: Byte = 0x1
        const val BOTTOM_SHEET_CHOOSE_PHOTO: Byte = 0x2

        const val INTENT_MAKE_CALL = "intent_make_call"
        const val INTENT_EDIT_CONTACT = "intent_edit_contact"
        const val INTENT_EDIT_CHANGE = "intent_has_change"
        const val INTENT_EDIT_RESULT = "intent_edit_result"
        const val INTENT_FREQUENT_COUNT = "intent_frequent_count"
    }
}