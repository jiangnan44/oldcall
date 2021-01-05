package com.v.oldcall.mvps

import android.graphics.Bitmap
import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2021/1/5
 */
class EditModel : EditContract.Model {
    override suspend fun updateInfo(
        name: String?,
        phone: String?,
        srcBitmap: Bitmap?,
        contact: ContactEntity
    ): Boolean {
        TODO("Not yet implemented")
    }


    private suspend fun updateSystemContact(): Boolean {

    }

    private suspend fun savePhoto2File(): Boolean {

    }

    private suspend fun updateDb(): Boolean {

    }

    override fun destroy() {
        TODO("Not yet implemented")
    }
}