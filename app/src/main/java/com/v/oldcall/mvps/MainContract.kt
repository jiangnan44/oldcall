package com.v.oldcall.mvps

import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2020/11/26
 */
class MainContract {
    interface Model : BaseContract.BaseModel {
        fun getContacts(): List<ContactEntity?>?
    }

    interface View : BaseContract.BaseView {
        fun showNoContacts(msg: String)
        fun updateContacts(contactList: List<ContactEntity?>?)
    }

    abstract class Presenter<V,M> : BaseContract.BasePresenter<V,M>() {
        abstract fun showFrequentContacts()
        abstract fun updateRecentList()
        abstract fun go2Call(phone: String)
    }


}