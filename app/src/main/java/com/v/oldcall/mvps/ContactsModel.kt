package com.v.oldcall.mvps

import android.content.ContentUris
import android.content.Context
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.ContactComparator
import com.v.oldcall.utils.ObjectBoxHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * Author:v
 * Time:2020/12/9
 */
class ContactsModel : ContactsContract.Model {

    private val name = android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    private val phone = android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
    private val rid = android.provider.ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
    private val photo = android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_ID
    private val uri = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    private var cacheList: MutableList<ContactEntity>? = ArrayList()


    override suspend fun getContacts(context: Context?): List<ContactEntity>? {
        if (context == null) return null

        if (cacheList!!.size > 0) {
            return cacheList
        }

        val frequentList =
            ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).query().build().find()

        val list: ArrayList<ContactEntity> = ArrayList()
        val cr = context.contentResolver
        val cursor = cr.query(uri, arrayOf(rid, photo, phone, name), null, null, null)
        cursor?.let {
            val indexName = it.getColumnIndex(name)
            val indexPhone = it.getColumnIndex(phone)
            val indexPhoto = it.getColumnIndex(photo)
            val indexId = it.getColumnIndex(rid)

            while (it.moveToNext()) {
                val ce = ContactEntity()
                ce.name = it.getString(indexName)
                ce.phone = it.getString(indexPhone)
                ce.cid = it.getLong(indexId)
                ce.pid = it.getLong(indexPhoto)
                if (ce.pid > 0) {
                    ce.avatar = ContentUris.withAppendedId(
                        android.provider.ContactsContract.Contacts.CONTENT_URI,
                        ce.cid
                    ).toString()
                }

                for (item in frequentList) {
                    if (ce.phone == item.phone) {
                        ce.isFrequent = true
                        ce.id = item.id
                        break
                    }
                }

                list.add(ce)
            }
            it.close()
        }
        if (list.isNotEmpty()) {
            Collections.sort(list, ContactComparator())
        }
        cacheList!!.addAll(list)
        return list
    }

    override suspend fun searchContacts(key: String): List<ContactEntity>? {
        val results: MutableList<ContactEntity> = ArrayList()
        val patten = Pattern.quote(key)
        val pattern = Pattern.compile(patten, Pattern.CASE_INSENSITIVE)
        cacheList?.let {
            for (item in it) {
                val matcherWord = pattern.matcher(item.alpha)
                val matcherPy = pattern.matcher(item.pinyin)
                val matcherPhone = pattern.matcher(item.phone)
                val matcherName = pattern.matcher(item.name)
                if (matcherWord.find() || matcherPy.find() || matcherName.find() || matcherPhone.find()) {
                    results.add(item)
                }
            }

        }
        return results
    }

    /**
     * no need database at all ,here is just a example demo
     */
    override suspend fun add2FrequentContacts(contact: ContactEntity): Boolean {
        if (contact.isFrequent || contact.name.isNullOrBlank()) {
            return false
        }
        var ret = -1L
        withContext(Dispatchers.IO) {
            contact.isFrequent = true
            ret = ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).put(contact)
        }
        return ret >= 0
    }

    override suspend fun removeFrequentContacts(contact: ContactEntity): Boolean {
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
        cacheList?.let {
            it.clear()
            cacheList = null
        }
    }


}