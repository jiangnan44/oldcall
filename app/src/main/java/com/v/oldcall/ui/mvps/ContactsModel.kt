package com.v.oldcall.ui.mvps

import android.content.ContentUris
import android.content.Context
import android.util.Log
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.ContactComparator
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
    private val id = android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID
    private val photo = android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_ID
    private val uri = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI

    private var cacheList: MutableList<ContactEntity>? = ArrayList()


    override suspend fun getContacts(context: Context?): List<ContactEntity>? {
        if (context == null) return null

        if (cacheList!!.size > 0) {
            return cacheList
        }

        val list: ArrayList<ContactEntity> = ArrayList()
        val cr = context.contentResolver
        val cursor = cr.query(uri, arrayOf(id, photo, phone, name), null, null, null)
        cursor?.let {
            val indexName = it.getColumnIndex(name)
            val indexPhone = it.getColumnIndex(phone)
            val indexPhoto = it.getColumnIndex(photo)
            val indexId = it.getColumnIndex(id)

            while (it.moveToNext()) {
                val ce = ContactEntity()
                ce.name = it.getString(indexName)
                ce.phone = it.getString(indexPhone)
                ce.id = it.getLong(indexId)
                val photo_id = it.getLong(indexPhoto)
                if (photo_id > 0) {
                    ce.avatar = ContentUris.withAppendedId(
                        android.provider.ContactsContract.Contacts.CONTENT_URI,
                        ce.id!!
                    )
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

    override suspend fun add2FrequentContacts(contact: ContactEntity): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        cacheList?.let {
            it.clear()
            cacheList = null
        }
    }


}