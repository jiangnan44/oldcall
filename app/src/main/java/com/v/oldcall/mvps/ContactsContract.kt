package com.v.oldcall.mvps

import android.content.Context
import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2020/12/7
 */
class ContactsContract {
    interface Model : BaseContract.BaseModel {
        suspend fun getContacts(context: Context?): List<ContactEntity>?
        suspend fun searchContacts(key: String): List<ContactEntity>?
        suspend fun add2FrequentContacts(contact: ContactEntity): Boolean
        suspend fun removeFrequentContacts(contact: ContactEntity): Boolean
    }

    interface View : BaseContract.BaseView {
        fun onContactsGot(list: List<ContactEntity>?)
        fun onContactsSearched(list: List<ContactEntity>?)
        fun onAddFrequentContacts(position: Int, success: Boolean)
        fun onRemoveFrequentContacts(position: Int, success: Boolean)
    }

    abstract class Presenter<V, M> : BaseContract.BasePresenter<V, M>() {
        abstract fun showContacts()
        abstract fun searchContacts(key: String)
        abstract fun add2FrequentContacts(contact: ContactEntity, position: Int)
        abstract fun removeFrequentContacts(contact: ContactEntity, position: Int)
    }
}