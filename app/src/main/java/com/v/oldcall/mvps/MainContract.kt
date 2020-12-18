package com.v.oldcall.mvps

import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2020/11/26
 */
class MainContract {
    interface Model : BaseContract.BaseModel {
        suspend fun getContacts(): List<ContactEntity>?
        suspend fun removeContact(contact: ContactEntity): Boolean
    }

    interface View : BaseContract.BaseView {
        fun showNoContacts(msg: String)
        fun onContactRemoved(removed: Boolean, position: Int)
        fun updateContacts(contactList: List<ContactEntity>)
    }

    abstract class Presenter<V, M> : BaseContract.BasePresenter<V, M>() {
        abstract fun showFrequentContacts()
        abstract fun updateRecentList()
        abstract fun removeContact(contact: ContactEntity, position: Int)
        abstract fun go2Call(phone: String)
    }


}