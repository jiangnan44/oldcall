package com.v.oldcall.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Author:v
 * Time:2020/12/30
 */
object BitmapUtil {
    private const val TAG = "bitmapUtil"

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        Log.d(TAG, "uri=${uri.toString()}")
        var inputStream = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().also {
            it.inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)


        val originalW = options.outWidth
        val originalH = options.outHeight
        Log.d(TAG, "originalW=$originalW---originalH=$originalH")
        if (originalH == -1 || originalW == -1) {
            return null
        }
        inputStream!!.close()

        val h = 800f
        val w = 480f
        var ratio = 1
        if (originalW > originalH && originalW > w) {
            ratio = (originalW / w).toInt()
        } else if (originalH > originalW && originalH > h) {
            ratio = (originalH / h).toInt()
        }
        if (ratio <= 0) ratio = 1

        val ratioOptions = BitmapFactory.Options().also {
            it.inDither = true
            it.inSampleSize = ratio
            it.inPreferredConfig = Bitmap.Config.RGB_565
        }
        inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream, null, ratioOptions)
        Log.d(TAG, "bitmap=$bitmap")
        inputStream!!.close()
        return compressBitmap(bitmap)
    }

    fun compressBitmap(srcBitmap: Bitmap?): Bitmap? {
        Log.d(TAG, "srcBitmap=$srcBitmap")
        if (srcBitmap == null) return null
        val baos = ByteArrayOutputStream()
        srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        var quality = 100
        while (baos.toByteArray().size > 300 * 1024) {//300K
            baos.reset()
            srcBitmap.compress(Bitmap.CompressFormat.PNG, quality, baos)
            quality -= 10
            if (quality <= 0) break
        }
        val bais = ByteArrayInputStream(baos.toByteArray())
        val desBitmap = BitmapFactory.decodeStream(bais, null, null)
        baos.close()
        bais.close()
        srcBitmap.recycle()
        return desBitmap
    }
}