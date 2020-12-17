package com.v.oldcall.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import com.v.oldcall.R
import com.v.oldcall.dialogs.DecisionDialog

/**
 * Author:v
 * Time:2020/12/2
 * doing launch thing,here mainly used to request permission,
 * so there is no UI
 */
class PermissionCheckActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PermissionCheckActivity"
        private const val REQUEST_CODE = 0x1
    }

    //this can be got from launch activity
    private val permissions: MutableList<String> = mutableListOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_CONTACTS
    )
    private var isFromResult = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shouldShowRequestPermission()
        for (v in window.decorView as ViewGroup) {
            v.visibility = View.INVISIBLE
        }
    }


    private fun shouldShowRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        var noDialogShowed = true
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                Log.w(TAG, "show shouldShowRequestPermission")
                showRationalDialog(permission)
                noDialogShowed = false
                break
            }
        }

        if (noDialogShowed) {
            Log.w(TAG, " noDialogShowed")
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        Log.w(TAG, "checkPermissions")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        val tmp = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                tmp.add(permission)
            }
        }
        permissions.removeAll(tmp)

        if (permissions.size == 0) {
            Log.w(TAG, "check success")
            checkSuccess()
        } else {
            if (isFromResult) {
                showLastWarningDialog()
            } else {
                requestPermission()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        Log.w(TAG, "request permission")
        val permissionList = Array<String>(permissions.size) { "" }
        for (i in 0 until permissions.size) {
            permissionList[i] = permissions[i]
        }
        requestPermissions(
            permissionList,
            REQUEST_CODE
        )
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE) return
        val deniedPermissions = ArrayList<String>(permissions.size)
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(permissions[i])) {
                    showRationalDialog(permissions[i])
                    return
                } else {
                    deniedPermissions.add(permissions[i])
                }
            }
        }
        Log.w(TAG, "onRequestPermissionsResult:grantResults.size=" + grantResults.size)

        if (deniedPermissions.size > 0) {
            showGoSettingDialog(deniedPermissions)
        } else {
            Log.w(TAG, "checkSuccess")
            checkSuccess()
        }

    }


    private fun showRationalDialog(permission: String) {
        lateinit var title: String
        lateinit var content: String
        val res = resources

        when (permission) {
            Manifest.permission.CALL_PHONE -> {
                title = res.getString(R.string.title_call_phone)
                content = res.getString(R.string.content_call_phone)
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                title = res.getString(R.string.title_storage)
                content = res.getString(R.string.content_storage)
            }

            Manifest.permission.WRITE_CONTACTS -> {
                title = res.getString(R.string.title_contacts)
                content = res.getString(R.string.content_write_contacts)
            }
        }

        DecisionDialog.Builder(this)
            .withTitle(title)
            .withContent(content)
            .withCancelOutside(false)
            .withCancelOnBack(false)
            .withLeftText(res.getString(R.string.cancel))
            .withRightText(res.getString(R.string.go_check))
            .withLeftListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    showRecheckDialog(permission)
                }
            })
            .withRightListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    checkPermissions()
                }
            })
            .build()
            .show()
    }


    private fun showLastWarningDialog() {
        Log.w(TAG, "showLastWarningDialog")
        val res = resources
        DecisionDialog.Builder(this)
            .withTitle(res.getString(R.string.title_crying_warning))
            .withContent(res.getString(R.string.content_crying_warning))
            .withLeftText(res.getString(R.string.cancel))
            .withRightText(res.getString(R.string.go_setting))
            .withCancelOutside(false)
            .withCancelOnBack(false)
            .withLeftListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    finish()
                }
            })
            .withRightListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    go2Setting()
                }
            })
            .build()
            .show()
    }

    private fun showRecheckDialog(permission: String) {
        Log.w(TAG, "showRecheckDialog")
        val res = resources
        val request =
            String.format(res.getString(R.string.content_recheck), getPermissionString(permission))

        DecisionDialog.Builder(this)
            .withTitle(res.getString(R.string.title_denied))
            .withContent(request)
            .withLeftText(res.getString(R.string.cancel))
            .withRightText(res.getString(R.string.go_check))
            .withCancelOnBack(false)
            .withCancelOutside(false)
            .withLeftListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    finish()
                }
            })
            .withRightListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    checkPermissions()
                }
            })
            .build()
            .show()
    }

    private fun showGoSettingDialog(permissions: ArrayList<String>) {
        Log.w(TAG, "handleDenied")
        val res = resources
        var request: String = ""
        for (p in permissions) {
            request += "${getPermissionString(p)}、"
        }
        request = request.substring(0, request.length - 1)

        request =
            String.format(res.getString(R.string.content_setting), request)

        DecisionDialog.Builder(this)
            .withTitle(res.getString(R.string.title_denied))
            .withContent(request)
            .withLeftText(res.getString(R.string.cancel))
            .withRightText(res.getString(R.string.go_setting))
            .withCancelOutside(false)
            .withCancelOnBack(false)
            .withLeftListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    finish()
                }
            })
            .withRightListener(object : DecisionDialog.DialogClickListener {
                override fun onClick(dialog: Dialog) {
                    go2Setting()
                }
            })
            .build()
            .show()
    }

    private fun getPermissionString(permission: String): String {
        return when (permission) {
            Manifest.permission.CALL_PHONE ->
                getString(R.string.permission_phone)

            Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                getString(R.string.permission_storage)

            Manifest.permission.WRITE_CONTACTS ->
                getString(R.string.permission_contacts)

            else -> {
                getString(R.string.permission_default)
            }
        }
    }


    private fun go2Setting() {
        val pkgUri = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, pkgUri)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE == requestCode) {
            isFromResult = true
            checkPermissions()
        }
    }


    private fun checkSuccess() {
        finish()
    }


}