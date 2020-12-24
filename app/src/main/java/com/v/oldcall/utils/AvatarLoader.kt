package com.v.oldcall.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.widget.ImageView
import androidx.collection.LruCache
import kotlinx.coroutines.*
import java.io.IOException

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

    fun loadAvatar(uri: String?, iv: ImageView?) {
        if (uri == null) return
        val avatar = getAvatarFromCache(uri)
        if (avatar == null) {
            loadAvatarWithCoroutine(uri, iv)
        } else {
            iv?.setImageBitmap(avatar)
        }

    }

    private fun loadAvatarWithCoroutine(uri: String, iv: ImageView?) {
        CoroutineScope(Dispatchers.Main).launch {
            val avatar = getAvatar(uri, iv?.context)
            avatar?.let {
                iv?.setImageBitmap(avatar)
                lruCache?.put(uri, avatar)
            }
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