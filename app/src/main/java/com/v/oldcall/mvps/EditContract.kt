package com.v.oldcall.mvps

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.v.oldcall.entities.ContactEntity

/**
 * Author:v
 * Time:2021/1/5
 */
class EditContract {
    interface Model : BaseContract.BaseModel {
        suspend fun updateInfo(
            name: String?,
            phone: String?,
            srcBitmap: Bitmap?,
            contact: ContactEntity
        ): Boolean
    }

    interface View : BaseContract.BaseView {
        fun saveInfoEnd(ret: Boolean)
        fun onCropPhotoDecoded(photo: Bitmap?)
    }

    abstract class Presenter<V, M> : BaseContract.BasePresenter<V, M>() {
        abstract fun saveInfo(
            name: String?,
            phone: String?,
            srcBitmap: Bitmap?,
            contact: ContactEntity
        )

        abstract fun cropRawPhoto(srcUri: Uri, requestCode: Int)
        abstract fun chooseAvatarFromGallery(requestCode: Int)
        abstract fun openCamera(requestCode: Int)
        abstract fun decodeCropPhoto()
    }
}