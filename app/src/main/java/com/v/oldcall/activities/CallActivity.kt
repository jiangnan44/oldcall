package com.v.oldcall.activities

import android.content.Intent
import android.database.ContentObserver
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.v.oldcall.R
import com.v.oldcall.app.App
import com.v.oldcall.constants.Keys
import com.v.oldcall.entities.ContactEntity
import com.v.oldcall.utils.*

class CallActivity : BaseActivity(), View.OnLongClickListener {
    companion object {
        private const val TAG = "CallActivity"
        private const val REQUEST_CODE_EDIT = 0x5
    }

    private var ivAvatar: ImageView? = null
    private var tvAvatar: TextView? = null
    private var tvName: TextView? = null
    private var tvPhone: TextView? = null
    private var ivCall: ImageView? = null

    private lateinit var mContact: ContactEntity
    private var isModifyAvatar = false


    @Suppress("DEPRECATION")
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
            go2EditContact()
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
        startActivityForResult(Intent(this, EditContactActivity::class.java).also {
            it.putExtra(Keys.INTENT_EDIT_CONTACT, mContact)
        }, REQUEST_CODE_EDIT)

    }


    override fun initData() {
        val tmp = intent?.getParcelableExtra<ContactEntity>(Keys.INTENT_MAKE_CALL)
        if (null == tmp) {
            ToastManager.showShort(this, getString(R.string.call_err_contact))
            finish()
            return
        }
        mContact = tmp
        Log.d(TAG, "data is:$mContact")
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
            tvAvatar?.visibility = View.GONE
            ivAvatar?.let {
                it.visibility = View.VISIBLE
                AvatarLoader.loadAvatar(mContact, it)
            }
        }

        tvPhone?.text = mContact.phone
        tvName?.text = mContact.name
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT) {
            data?.let {
                val change = it.getBooleanExtra(Keys.INTENT_EDIT_CHANGE, false)
                if (change) {
                    mContact = it.getParcelableExtra(Keys.INTENT_EDIT_RESULT)!!
                    updateView()
                    App.needRefreshFrequentContacts = true
                }
            }
        }
    }


    private fun go2Call(phone: String) {
        startActivity(Intent().apply {
            action = Intent.ACTION_CALL
            data = Uri.parse("tel:$phone")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}