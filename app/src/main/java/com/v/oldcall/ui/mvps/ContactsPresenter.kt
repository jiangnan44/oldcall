package com.v.oldcall.ui.mvps

import android.content.Context
import android.util.Log
import com.v.oldcall.entities.ContactEntity
import kotlinx.coroutines.*
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

/**
 * Author:v
 * Time:2020/12/9
 */
class ContactsPresenter<V : ContactsContract.View> :
    ContactsContract.Presenter<V, ContactsModel>() {
    private val scope = CoroutineScope(Dispatchers.Main)

    fun startCoroutine(
        context: CoroutineContext = Dispatchers.Main,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = scope.launch(
        context, start, block
    )

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

    override fun add2FrequentContacts(contact: ContactEntity) {
        TODO("Not yet implemented")
    }

    override fun detach() {
        scope.cancel()
        super.detach()
    }

}