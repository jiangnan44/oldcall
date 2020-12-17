package com.v.oldcall.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.v.oldcall.R
import com.v.oldcall.dialogs.LoadingDialog

/**
 * Author:v
 * Time:2020/11/25
 */
abstract class BaseActivity : AppCompatActivity() {

    private var loadingDialog: LoadingDialog? = null
    protected var mToolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentLayoutId())
        initToolbar()
        initView()
        initData()
    }

    private fun initToolbar() {
        mToolbar = findViewById(R.id.toolbar)
        mToolbar?.setNavigationOnClickListener {
            finish()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.let {
            if (it.isShowing) it.dismiss()
            loadingDialog = null
        }
    }

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.show()
    }

    fun dismissLoading() {
        loadingDialog?.let {
            if (it.isShowing) it.dismiss()
        }
    }

    fun setToolbarTitle(title: String) {
        mToolbar?.title = title
    }

    /**
     * @param icon null to clear the icon
     */
    fun setNavigationIcon(@Nullable icon: Drawable?) {
        mToolbar?.navigationIcon = icon
    }

    fun setNavigationClickListener(listener: View.OnClickListener) {
        mToolbar?.setNavigationOnClickListener(listener)
    }

    abstract fun getContentLayoutId(): Int
    abstract fun initView()
    open fun initData() {}
}