package com.v.oldcall.ui.mvps

import android.content.ContentUris
import android.content.Context
import android.util.Log
import com.v.oldcall.entities.ContactEntity

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


    override suspend fun getContacts(context: Context?): List<ContactEntity?>? {
        if (context == null) return null
        Log.w("vvv","getContacts")
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
        return list
    }

    override suspend fun searchContacts(): List<ContactEntity?>? {
        TODO("Not yet implemented")
    }

    override suspend fun add2FrequentContacts(contact: ContactEntity): Boolean {
        TODO("Not yet implemented")
    }
}