package com.v.oldcall.mvps

import android.util.Log
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.ObjectBoxHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Author:v
 * Time:2020/11/26
 */
class MainModel : MainContract.Model {

    override suspend fun getContacts(): List<ContactEntity> {
        var list: MutableList<ContactEntity>
        withContext(Dispatchers.IO) {
            list = ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).query().build().find()
        }
        return list
    }

    override suspend fun removeContact(contact: ContactEntity): Boolean {
        if (/*!contact.isFrequent ||*/ contact.name.isNullOrBlank()) {
            return false
        }
        Log.w("vvv", "mainModel remove id=" + contact.id)
        var ret = false
        withContext(Dispatchers.IO) {
            ret = ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).remove(contact.id)
        }
        return ret
    }

    override fun destroy() {
        TODO("Not yet implemented")
    }

}