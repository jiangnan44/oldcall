package com.v.oldcall.mvps

import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.util.Log
import com.v.oldcall.app.App
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.FileUtil
import com.v.oldcall.utils.ObjectBoxHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Author:v
 * Time:2021/1/5
 */
class EditModel : EditContract.Model {

    override suspend fun updateInfo(
        name: String,
        phone: String,
        srcBitmap: Bitmap?,
        contact: ContactEntity
    ): Boolean = withContext(Dispatchers.IO) {
        val r1 = async { savePhoto(srcBitmap, contact) }
        val r2 = async { updateSystemContact(name, phone, contact) }

        r1.await() && r2.await()
    }


    private suspend fun updateSystemContact(
        name: String,
        phone: String,
        contact: ContactEntity
    ): Boolean = withContext(Dispatchers.IO) {

        if (name.isEmpty() && phone.isEmpty()) {
            return@withContext true
        }

        val values = ContentValues()
        val where =
            ContactsContract.Data.MIMETYPE + " = ? and " + ContactsContract.Data.RAW_CONTACT_ID + " = ? "

        val r1 = if (name.isNotEmpty()) {
            values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
            val param = arrayOf(
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                contact.cid.toString()
            )
            try {
                App.instance().contentResolver.update(
                    ContactsContract.Data.CONTENT_URI, values, where, param
                )
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        } else {
            1
        }

        val r2 = if (phone.isNotEmpty()) {
            values.clear()
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
            try {
                App.instance().contentResolver.update(
                    ContactsContract.Data.CONTENT_URI,
                    values,
                    where,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        contact.cid.toString()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        } else {
            1
        }
        r1 > 0 && r2 > 0
    }


    private suspend fun savePhoto(srcBitmap: Bitmap?, contact: ContactEntity): Boolean =
        withContext(Dispatchers.IO) {
            if (srcBitmap == null) return@withContext true

            val os = ByteArrayOutputStream()
            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            val avatar = os.toByteArray()

            val success = savePhoto2ContactAndDir(avatar, contact)
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.d("EditContactActivity", "success avatar= ${contact.avatar}")
            if (!success) return@withContext false
            val ret = ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).put(contact)
            ret > 0
        }

    private fun savePhoto2ContactAndDir(
        avatar: ByteArray,
        contact: ContactEntity
    ): Boolean {
        val values = ContentValues()
        if (contact.pid > 0) {
            values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, avatar)

            val ret = App.instance().contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                values,
                ContactsContract.RawContacts.Data.MIMETYPE + " =? AND " + ContactsContract.RawContacts.Data.RAW_CONTACT_ID + " = " + contact.cid,
                arrayOf(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            )

            if (ret < 0) {
                return false
            }

            return FileUtil.saveBitmap2File(App.instance(), avatar, contact.phone)

        } else {
            values.also {
                it.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, contact.cid)
                it.put(
                    ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                )
                it.put(ContactsContract.CommonDataKinds.Photo.PHOTO, avatar)
            }

            App.instance().contentResolver.insert(
                ContactsContract.Data.CONTENT_URI,
                values
            ) ?: return false

            val temp = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI,
                contact.cid
            ).toString()
            contact.avatar = temp
            return FileUtil.saveBitmap2File(App.instance(), avatar, contact.phone)
        }
    }

    override fun destroy() {
        //nothing to do
    }
}