package com.v.oldcall.mvps

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
        if (!contact.isFrequent || contact.name.isNullOrBlank()) {
            return false
        }
        var ret: Boolean
        withContext(Dispatchers.IO) {
            ret = try {
                ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).remove(contact.id)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        return ret
    }

    override fun destroy() {
        //nothing here
    }

}