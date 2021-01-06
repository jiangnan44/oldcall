package com.v.oldcall.mvps

import android.graphics.Bitmap
import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2021/1/5
 */
class EditContract {
    interface Model : BaseContract.BaseModel {
        suspend fun updateInfo(
            name: String,
            phone: String,
            srcBitmap: Bitmap?,
            contact: ContactEntity
        ): Boolean
    }

    interface View : BaseContract.BaseView {
        fun onSaveInfoEnd(ret: Boolean)
        fun onCropPhotoDecoded(photo: Bitmap?)
    }

    abstract class Presenter<V, M> : BaseContract.BasePresenter<V, M>() {
        abstract fun saveInfo(
            name: String?,
            phone: String?,
            srcBitmap: Bitmap?,
            contact: ContactEntity
        )

        abstract fun decodeCropPhoto()
    }
}