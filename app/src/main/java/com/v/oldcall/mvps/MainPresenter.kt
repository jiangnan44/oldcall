package com.v.oldcall.mvps

import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2020/11/26
 */
class MainPresenter<V : MainContract.View> : MainContract.Presenter<V, MainModel>() {
    init {
        mModel = MainModel()
    }

    override fun showFrequentContacts() {
        mView?.showLoading()
        startCoroutine {
            val list = mModel?.getContacts()
            mView?.dismissLoading()
            if (list == null || list.isEmpty()) {
                mView?.showNoContacts("获取联系人失败")
            } else {
                mView?.updateContacts(list)
            }
        }
    }

    override fun updateRecentList() {
        TODO("Not yet implemented")
    }

    override fun go2Call(phone: String) {
        TODO("Not yet implemented")
    }

    override fun removeContact(contact: ContactEntity, position: Int) {
        startCoroutine {
            mView?.onContactRemoved(mModel?.removeContact(contact) ?: false, position)
        }
    }

}