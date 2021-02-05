package com.v.oldcall.mvps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.v.oldcall.app.App
import com.v.oldcall.app.BaseApplication
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

/**
 * Author:v
 * Time:2021/1/5
 */
class EditPresenter<V : EditContract.View> : EditContract.Presenter<V, EditModel>() {
    companion object {
        private const val TEMP_AVATAR_FILE_NAME = "temp_avatar.jpg"
        private const val TEMP_CROP_FILE_NAME = "temp_crop.jpg"
        private const val TYPE_IMAGE = "image/*"
    }

    init {
        mModel = EditModel()
    }

    internal var avatarUri: Uri? = null
    private var cropUri: Uri? = null

    override fun saveInfo(
        name: String?,
        phone: String?,
        srcBitmap: Bitmap?,
        contact: ContactEntity
    ) {
        mView?.showLoading()
        startCoroutine {
            val ret = async {
                mModel?.run {
                    val n = if (name == contact.name) {
                        ""
                    } else {
                        contact.name = name
                        name
                    }

                    val p = if (phone == contact.phone) {
                        ""
                    } else {
                        contact.phone = phone
                        phone
                    }
                    updateInfo(n!!, p!!, srcBitmap, contact)
                }
            }
            ret.await().let {
                mView?.dismissLoading()
                mView?.onSaveInfoEnd(it ?: false)
            }
        }
    }

    fun cropRawPhoto(srcUri: Uri, requestCode: Int) {
        Log.d("EditContactActivity", "cropRawPhoto")
        val tempPicture = File(
            FileUtil.getAppPictureCacheDir(App.instance()),
            TEMP_CROP_FILE_NAME
        )
        cropUri = FileUtil.getFileUri(tempPicture)
        val intent = Intent("com.android.camera.action.CROP").also {
            it.setDataAndType(srcUri, TYPE_IMAGE)
            it.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            it.putExtra("aspectX", 1)//ratio
            it.putExtra("aspectY", 1)
            it.putExtra("outputX", 480)//size
            it.putExtra("outputY", 480)
            it.putExtra("scale", true)

            it.putExtra("return-data", false)//no thumbnail got from back intent
            it.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name)
            it.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)
        }
        (mView as Activity?)?.startActivityForResult(intent, requestCode)
    }

    fun requestCameraPermission(requestCode: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            (mView as Activity?)?.requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                requestCode
            )
        }
    }


    fun chooseAvatarFromGallery(requestCode: Int) {
        (mView as Activity?)?.startActivityForResult(Intent().also {
            it.action = Intent.ACTION_PICK
            it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, TYPE_IMAGE)
        }, requestCode)
    }

    fun openCamera(requestCode: Int) {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val tempPicture = File(
            FileUtil.getAppPictureCacheDir(App.instance()),
            TEMP_AVATAR_FILE_NAME
        )

        avatarUri = FileUtil.getProviderFileUri(App.instance(), tempPicture)
        intentCamera.putExtra(
            MediaStore.EXTRA_OUTPUT,
            avatarUri
        )
        intentCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        (mView as Activity?)?.startActivityForResult(intentCamera, requestCode)
    }

    override fun decodeCropPhoto() {
        cropUri?.let {
            startCoroutine(Dispatchers.IO) {
                var bitmap: Bitmap? = null
                try {
                    bitmap = BitmapFactory.decodeStream(
                        App.instance().contentResolver.openInputStream(it)
                    )
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                withContext(Dispatchers.Main) {
                    mView?.onCropPhotoDecoded(bitmap)
                }
            }
        }
    }
}