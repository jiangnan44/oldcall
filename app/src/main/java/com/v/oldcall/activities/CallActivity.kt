package com.v.oldcall.activities

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.RawContacts
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.v.oldcall.R
import com.v.oldcall.constants.Keys
import com.v.oldcall.dialogs.BottomListDialog
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

class CallActivity : BaseActivity(), View.OnLongClickListener {
    companion object {
        private const val TAG = "CallActivity"
        private const val REQUEST_CODE_CAPTURE = 0x1
        private const val REQUEST_CODE_GALLERY = 0x2
        private const val REQUEST_CODE_CAMERA = 0x3
        private const val REQUEST_CODE_CORP = 0x4
        private const val REQUEST_CODE_EDIT = 0x5
        private const val TEMP_AVATAR_FILE_NAME = "temp_avatar.jpg"
        private const val TEMP_CROP_FILE_NAME = "temp_crop.jpg"
        private const val TYPE_IMAGE = "image/*"
    }

    private var chooseModifyDialog: BottomListDialog? = null
    private var ivAvatar: ImageView? = null
    private var tvAvatar: TextView? = null
    private var tvName: TextView? = null
    private var tvPhone: TextView? = null
    private var ivCall: ImageView? = null

    private lateinit var mObserver: ContentObserver
    private lateinit var mContact: ContactEntity
    private var avatarUri: Uri? = null
    private var cropUri: Uri? = null
    private var isModifyAvatar = false


    private fun setImmersive() {
        with(window) {
            val flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            decorView.systemUiVisibility = flag
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_call
    }

    override fun initView() {
        ivAvatar = findViewById(R.id.ac_iv_avatar)
        tvAvatar = findViewById(R.id.ac_tv_avatar)
        tvName = findViewById(R.id.ac_tv_name)
        tvPhone = findViewById(R.id.ac_tv_phone)
        ivCall = findViewById(R.id.ac_iv_call)
        ivCall!!.setOnClickListener {
            go2Call(mContact.phone!!)
        }
        ivAvatar!!.setOnLongClickListener(this)
        tvAvatar!!.setOnLongClickListener(this)
        tvName!!.setOnLongClickListener(this)
        tvPhone!!.setOnLongClickListener(this)
        tvAvatar!!.setOnClickListener {
            showModifyAvatarDialog()
        }
        setImmersive()
    }


    override fun onLongClick(v: View?): Boolean {
        when (v?.id) {
            R.id.ac_tv_avatar,
            R.id.ac_iv_avatar,
            R.id.ac_tv_name,
            R.id.ac_tv_phone -> {
                go2EditContact()
            }
        }

        return true
    }

    private fun go2EditContact() {
        val uri = Uri.parse("content://com.android.contacts/contacts/" + mContact.cid)
        startActivityForResult(Intent(this, EditContactActivity::class.java).also {
            it.putExtra(Keys.INTENT_EDIT_CONTACT, mContact)
        }, REQUEST_CODE_EDIT)
        if (!::mObserver.isInitialized) {
            mObserver = EditContactObserver(null)
            contentResolver.registerContentObserver(uri, true, mObserver)
        }
    }


    override fun initData() {
        val tmp = intent?.getParcelableExtra<ContactEntity>(Keys.INTENT_MAKE_CALL)
        if (null == tmp) {
            ToastManager.showShort(this, getString(R.string.call_err_contact))
            finish()
            return
        }
        mContact = tmp
        Log.w(TAG, "data is:$mContact")
        isModifyAvatar = intent?.getBooleanExtra(Keys.INTENT_MODIFY_AVATAR, false) ?: false
        updateView()
    }

    private fun updateView() {
        if (mContact.avatar == null) {
            ivAvatar?.visibility = View.INVISIBLE
            tvAvatar?.let {
                it.visibility = View.VISIBLE
                it.text = mContact.name
                it.setBackgroundColor(
                    ContextCompat.getColor(
                        this@CallActivity,
                        mContact.avatarColor
                    )
                )
            }
        } else {
            AvatarLoader.loadAvatar(mContact, ivAvatar)
        }

        tvPhone?.text = mContact.phone
        tvName?.text = mContact.name
    }

    private fun getSheetItems(): List<BottomListDialog.SheetItem> {
        return listOf(
            BottomListDialog.SheetItem(
                getString(R.string.call_choose_photo),
                Keys.BOTTOM_SHEET_CHOOSE_PHOTO
            ),
            BottomListDialog.SheetItem(
                getString(R.string.call_take_photo),
                Keys.BOTTOM_SHEET_TAKE_PHOTO
            )
        )
    }

    private fun showModifyAvatarDialog() {
        if (chooseModifyDialog == null) {
            chooseModifyDialog = BottomListDialog(this).also {
                it.setListItems(getSheetItems())
                it.setListItemClickListener(object : BottomListDialog.OnListItemClickListener {
                    override fun onItemClick(action: Byte) {
                        when (action) {
                            Keys.BOTTOM_SHEET_TAKE_PHOTO -> {
                                getAvatarFromCamera()
                            }
                            Keys.BOTTOM_SHEET_CHOOSE_PHOTO -> {
                                chooseAvatarFromGallery()
                            }
                        }
                    }
                })
            }
        }
        chooseModifyDialog!!.show()
    }

    private fun getAvatarFromCamera() {
        if (PermissionUtil.hasCameraPermission(this)) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun openCamera() {
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val tempPicture = File(FileUtil.getAppPictureCacheDir(this), TEMP_AVATAR_FILE_NAME)

        avatarUri = FileUtil.getProviderFileUri(this, tempPicture)
        Log.w(TAG, "avatarUti=${avatarUri.toString()}")
        intentCamera.putExtra(
            MediaStore.EXTRA_OUTPUT,
            avatarUri
        )
        intentCamera.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intentCamera, REQUEST_CODE_CAPTURE)
    }

    private fun requestCameraPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        }
    }

    private fun chooseAvatarFromGallery() {
        startActivityForResult(Intent().also {
            it.action = Intent.ACTION_PICK
            it.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, TYPE_IMAGE)
        }, REQUEST_CODE_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.w(TAG, "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CANCELED) {
            ToastManager.showShort(this, getString(R.string.cancel))
            return
        }

        when (requestCode) {
            REQUEST_CODE_CAPTURE -> {
                avatarUri?.let {
                    cropRawPhoto(it)
                }
            }

            REQUEST_CODE_GALLERY -> {
                val uri = data?.data
                uri?.let {
                    cropRawPhoto(it)
                }
            }

            REQUEST_CODE_CORP -> {
                cropUri?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        var bitmap: Bitmap? = null
                        try {
                            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(it))
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        updateAvatar(bitmap)
                    }
                }
            }

            REQUEST_CODE_EDIT -> {
                data?.let {
                    val change = it.getBooleanExtra(Keys.INTENT_EDIT_CHANGE, false)
                    if (change) {
                        mContact = it.getParcelableExtra<ContactEntity>(Keys.INTENT_EDIT_CONTACT)!!
                        updateView()
                    }
                }
                ToastManager.showShort(this, "back....")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            for (p in grantResults) {
                if (p == PackageManager.PERMISSION_DENIED) {
                    ToastManager.showLong(this, "go open camera permission")
                } else {
                    openCamera()
                }
            }
        }
    }

    private fun cropRawPhoto(srcUri: Uri) {
        val tempPicture = File(FileUtil.getAppPictureCacheDir(this), TEMP_CROP_FILE_NAME)
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
        startActivityForResult(intent, REQUEST_CODE_CORP)
    }

    private suspend fun updateAvatar(srcBitmap: Bitmap?) {
        Log.w(TAG, "updateAvatar")
        if (srcBitmap == null) {
            runOnUiThread {
                ToastManager.showShort(this, getString(R.string.modify_avatar_failed))
            }
            return
        }
        runOnUiThread {
            showLoading()
        }
        val os = ByteArrayOutputStream()
        srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        val avatar = os.toByteArray()

        val values = ContentValues()
        if (mContact.pid > 0) {
            values.put(CommonDataKinds.Photo.PHOTO, avatar)

            val ret = contentResolver.update(
                ContactsContract.Data.CONTENT_URI,
                values,
                RawContacts.Data.MIMETYPE + " =? AND " + RawContacts.Data.RAW_CONTACT_ID + " = " + mContact.cid,
                arrayOf(CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            )

            if (ret > 0) {
                updateAvatarSuccess(srcBitmap, avatar)
            } else {
                updateAvatarFailed()

            }

        } else {
            values.also {
                it.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, mContact.cid)
                it.put(
                    ContactsContract.Contacts.Data.MIMETYPE,
                    CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                )
                it.put(CommonDataKinds.Photo.PHOTO, avatar)
            }

            val ret = contentResolver.insert(ContactsContract.Data.CONTENT_URI, values)
            if (ret != null) {
                val temp = ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    mContact.cid
                ).toString()
                Log.w(TAG, "temp aka old=$temp")
                mContact.avatar = temp
                updateAvatarSuccess(srcBitmap, avatar)
            } else {
                updateAvatarFailed()
            }
        }
        try {
            os.close()
        } catch (e: Exception) {
        }
    }


    private suspend fun updateAvatarFailed() {
        withContext(Dispatchers.Main) {
            dismissLoading()
            ToastManager.showShort(this@CallActivity, "modify Failed!")
        }
    }

    private suspend fun updateAvatarSuccess(srcBitmap: Bitmap, avatar: ByteArray) {
        ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).put(mContact)
        FileUtil.saveBitmap2File(this, avatar, mContact.phone)
        withContext(Dispatchers.Main) {
            dismissLoading()
            ToastManager.showShort(this@CallActivity, "modify success")
            ivAvatar?.visibility = View.VISIBLE
            ivAvatar?.setImageBitmap(srcBitmap)
            tvAvatar?.visibility = View.GONE
        }
    }

    private fun go2Call(phone: String) {
        startActivity(Intent().apply {
            action = Intent.ACTION_CALL
            data = Uri.parse("tel:$phone")
        })
    }

    override fun onDestroy() {
        chooseModifyDialog?.let {
            it.dismiss()
            chooseModifyDialog = null
        }
        super.onDestroy()
    }

    private inner class EditContactObserver : ContentObserver {
        constructor(handler: Handler?) : super(handler)

        override fun onChange(selfChange: Boolean) {
            Log.w(TAG, "onchange selfChange=$selfChange,1111111i")

        }

    }

}