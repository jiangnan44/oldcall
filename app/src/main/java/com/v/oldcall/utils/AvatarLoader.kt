package com.v.oldcall.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView
import androidx.collection.LruCache
import com.v.oldcall.entities.ContactEntity
import kotlinx.coroutines.*

/**
 * Author:v
 * Time:2020/12/16
 */
object AvatarLoader {
    private var lruCache: LruCache<String, Bitmap>? = null

    init {
        val maxMemory = Runtime.getRuntime().maxMemory()
        val maxSize = maxMemory / 8
        lruCache = LruCache(maxSize.toInt())
    }


    private fun getAvatarFromCache(key: String) = lruCache?.get(key)

    /**
     * @param key phone or avatar
     */
    fun removeAvatarCache(key: String){
        lruCache?.remove(key)
    }

    fun loadAvatar(contact: ContactEntity, iv: ImageView?) {
        val avatar = getAvatarFromCache(contact.phone!!)
        if (avatar == null) {
            tryLoadAvatarFromFile(contact, iv)
        } else {
            iv?.setImageBitmap(avatar)
        }
    }

    private fun tryLoadAvatarFromFile(contact: ContactEntity, iv: ImageView?) {
        CoroutineScope(Dispatchers.Main).launch {
            val avatar = getAvatarFromFile(contact.phone!!, iv?.context)
            if (avatar != null) {
                iv?.setImageBitmap(avatar)
                lruCache?.put(contact.phone!!, avatar)
            } else {
                loadAvatarDb(contact.avatar!!, iv)
            }
        }
    }

    private suspend fun loadAvatarDb(uri: String, iv: ImageView?) {
        val avatar = getAvatarFromCache(uri)
        if (avatar == null) {
            loadAvatarFromDb(uri, iv)
        } else {
            iv?.setImageBitmap(avatar)
        }
    }

    private suspend fun getAvatarFromFile(phone: String, context: Context?): Bitmap? {
        if (context == null) return null
        var ret: Bitmap?
        withContext(Dispatchers.IO) {
            ret = FileUtil.getBitmapFromCacheFile(context, phone)
        }
        return ret
    }

    private suspend fun loadAvatarFromDb(uri: String, iv: ImageView?) {
        val avatar = getAvatar(uri, iv?.context)
        avatar?.let {
            iv?.setImageBitmap(avatar)
            lruCache?.put(uri, avatar)
        }
    }

    private suspend fun getAvatar(uri: String, context: Context?): Bitmap? {
        var ret: Bitmap?
        withContext(Dispatchers.IO) {
            val cr = context?.contentResolver
            val path = Uri.parse(uri)
            val input = ContactsContract.Contacts.openContactPhotoInputStream(cr, path)
            ret = BitmapFactory.decodeStream(input)
        }
        return ret
    }


    fun destroy() {
        lruCache?.let {
            it.evictAll()
            lruCache = null
        }
    }
}