package com.v.oldcall.mvps

import android.content.Context
import com.v.oldcall.entities.ContactEntity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Author:v
 * Time:2020/12/9
 */
class ContactsPresenter<V : ContactsContract.View> :
    ContactsContract.Presenter<V, ContactsModel>() {


    init {
        mModel = ContactsModel()
    }


    override fun showContacts() {
        mView?.showLoading()
        startCoroutine {
            val list = async { mModel?.getContacts(mView as Context) }
            list.await().let {
                mView?.dismissLoading()
                mView?.onContactsGot(it)
            }
        }
    }

    override fun searchContacts(key: String) {
        startCoroutine {
            mView?.onContactsSearched(mModel?.searchContacts(key))
        }
    }

    override fun add2FrequentContacts(contact: ContactEntity, position: Int) {
        startCoroutine {
            mView?.onAddFrequentContacts(
                position,
                mModel?.add2FrequentContacts(contact) ?: false
            )
        }
    }


    override fun removeFrequentContacts(contact: ContactEntity, position: Int) {
        startCoroutine {
            mView?.onRemoveFrequentContacts(
                position,
                mModel?.removeFrequentContacts(contact) ?: false
            )
        }
    }

    override fun detach() {
        super.detach()
        mModel?.destroy()
    }
}