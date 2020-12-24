package com.v.oldcall.activities

import android.content.Intent
import android.net.Uri
import com.v.oldcall.R

class CallActivity : BaseActivity() {



    override fun getContentLayoutId(): Int {
        return R.layout.activity_call
    }

    override fun initView() {
        TODO("Not yet implemented")
    }

     override fun initData() {
        TODO("Not yet implemented")
    }


    private fun go2Call(phone: String) {
        startActivity(Intent().apply {
            action = Intent.ACTION_CALL
            data = Uri.parse("tel:$phone")
        })
    }
}