package com.v.oldcall.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.v.oldcall.R
import com.v.oldcall.constants.Keys
import com.v.oldcall.dialogs.BottomListDialog
import com.v.oldcall.dialogs.DecisionDialog
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.mvps.EditContract
import com.v.oldcall.mvps.EditPresenter
import com.v.oldcall.utils.*
import java.lang.Exception


class EditContactActivity : BaseMvpActivity<EditPresenter<EditContract.View>>(),
    EditContract.View, View.OnClickListener {
    companion object {
        private const val TAG = "EditContactActivity"
        private const val REQUEST_CODE_CAPTURE = 0x1
        private const val REQUEST_CODE_GALLERY = 0x2
        private const val REQUEST_CODE_CORP = 0x3
        private const val REQUEST_PERMISSION_CAMERA = 0x4
    }


    private lateinit var ivAvatar: ImageView
    private lateinit var tvAvatar: TextView
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText

    private var chooseModifyDialog: BottomListDialog? = null
    private lateinit var mContact: ContactEntity
    private var mPhoto: Bitmap? = null
    private var hasModifyAvatar = false


    override fun initPresenter() {
        mPresenter = EditPresenter<EditContract.View>().also {
            it.attach(this)
        }
    }

    override fun getContentLayoutId(): Int {
        return R.layout.activity_edit_contact
    }

    override fun initView() {
        setToolbarTitle(R.string.edit_contact_title)
        ivAvatar = findViewById(R.id.aec_iv_avatar)
        tvAvatar = findViewById(R.id.aec_tv_avatar)
        etName = findViewById(R.id.aec_et_name)
        etPhone = findViewById(R.id.aec_et_phone)

        tvAvatar.setOnClickListener(this)
        ivAvatar.setOnClickListener(this)
        findViewById<View>(R.id.aec_tv_modify_avatar).setOnClickListener(this)
        findViewById<View>(R.id.aec_btn_save).setOnClickListener(this)
    }

    override fun initData() {
        val tmp = intent?.getParcelableExtra<ContactEntity>(Keys.INTENT_EDIT_CONTACT)
        if (null == tmp) {
            ToastManager.showShort(this, getString(R.string.call_err_contact))
            finish()
            return
        }
        mContact = tmp
        Log.w(TAG, "data is:$mContact")
        updateView()
    }

    private fun updateView() {
        if (mContact.avatar == null) {
            ivAvatar.visibility = View.INVISIBLE
            tvAvatar.let {
                it.visibility = View.VISIBLE
                it.text = mContact.name
                it.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        mContact.avatarColor
                    )
                )
            }
        } else {
            AvatarLoader.loadAvatar(mContact, ivAvatar)
        }

        etName.setText(mContact.name)
        etPhone.setText(mContact.phone)
    }

    override fun onSaveInfoEnd(success: Boolean) {
        if (success) {
            ToastManager.showShort(this, R.string.edit_save_success)
            updateDbData()
            finish()
        } else {
            showFailedDialog()
        }

    }

    private fun updateDbData() {
        val ret = ObjectBoxHelper.boxStore.boxFor(ContactEntity::class.java).get(mContact.id)
        Log.w(TAG, "onSaveInfoEnd,new data=$ret")

    }


    private fun showFailedDialog() {
        DecisionDialog.Builder(this)
            .withCancelOnBack(false)
            .withCancelOutside(false)
            .withTitle(getString(R.string.edit_save_failed))
            .withContent(getString(R.string.edit_go2_system_contact))
            .withLeftText(getString(R.string.cancel))
            .withRightText(getString(R.string.edit_go2_edit))
            .withLeftListener {
                finish()
            }
            .withRightListener {
                go2EditContact()
                removeContactAndExitApp()
            }
            .build().show()
    }


    private fun go2EditContact() {
        startActivity(Intent().also {
            it.action = Intent.ACTION_EDIT
            it.data = Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI,
                mContact.cid.toString()
            )
        })

    }

    private fun removeContactAndExitApp() {

    }

    override fun onCropPhotoDecoded(photo: Bitmap?) {
        if (photo == null) {
            ToastManager.showShort(this, getString(R.string.modify_avatar_failed))
            return
        }
        mPhoto?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
        mPhoto = photo
        updateAvatar()
    }

    private fun updateAvatar() {
        hasModifyAvatar = true
        if (ivAvatar.visibility != View.VISIBLE) {
            ivAvatar.visibility = View.VISIBLE
            tvAvatar.visibility = View.GONE
        }
        ivAvatar.setImageBitmap(mPhoto)
    }

    override fun onError(msg: String) {
        TODO("Not yet implemented")
    }


    override fun onDestroy() {
        super.onDestroy()
        mPhoto?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
            mPhoto = null
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.aec_btn_save -> {
                if (!checkInput()) {
                    return
                }
                if (!hasChanged()) {
                    return
                }

                mPresenter?.saveInfo(
                    etName.text.toString(),
                    etPhone.text.toString(),
                    mPhoto,
                    mContact
                )
            }

            R.id.aec_iv_avatar,
            R.id.aec_tv_modify_avatar -> {
                showModifyAvatarDialog()
            }

        }
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
                mPresenter?.let {
                    it.cropRawPhoto(it.avatarUri!!, REQUEST_CODE_CORP)
                }
            }

            REQUEST_CODE_GALLERY -> {
                val uri = data?.data
                uri?.let {
                    mPresenter?.cropRawPhoto(it, REQUEST_CODE_CORP)
                }
            }

            REQUEST_CODE_CORP -> {
                mPresenter?.decodeCropPhoto()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            for (p in grantResults) {
                if (p == PackageManager.PERMISSION_DENIED) {
                    ToastManager.showLong(this, "go open camera permission")
                } else {
                    mPresenter?.openCamera(REQUEST_CODE_CAPTURE)
                }
            }
        }
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
                                mPresenter?.chooseAvatarFromGallery(REQUEST_CODE_GALLERY)
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
            mPresenter?.openCamera(REQUEST_CODE_CAPTURE)
        } else {
            mPresenter?.requestCameraPermission(REQUEST_PERMISSION_CAMERA)
        }
    }

    private fun hasChanged(): Boolean {
        if (hasModifyAvatar) {
            return true
        }
        return mContact.name != etName.text.toString() || mContact.phone != etPhone.text.toString()
    }

    private fun checkInput(): Boolean {
        if (etName.text.isEmpty()) {
            ToastManager.showShort(this, R.string.edit_hint_empty_name)
            return false
        }

        val phone = etPhone.text
        if (phone.isEmpty()) {
            ToastManager.showShort(this, R.string.edit_hint_empty_phone)
            return false
        }
        val p = phone.replace(" ".toRegex(), "")
        if (!CommonUtil.isPhone(p)) {
            ToastManager.showShort(this, R.string.edit_hint_not_phone)
            return false
        }
        val desPhone = StringBuilder().also {
            it.append(p.subSequence(0, 3))
                .append(" ")
                .append(p.subSequence(3, 7))
                .append(" ")
                .append(p.substring(7))
        }
        etPhone.setText(desPhone.toString())
        return true
    }
}