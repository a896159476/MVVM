package com.epuxun.mvvm.base.permissions

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class PermissionsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.run {
            return with(AlertDialog.Builder(this)) {
                setTitle("提示")
                setMessage("在设置-应用-权限中开启所需权限，以保证功能的正常使用")
                // 拒绝, 退出应用
                setNegativeButton("退出") { _, _ ->
                    finish()
                }
                setPositiveButton("设置") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                    dismiss()
                }
                setCancelable(false)
                create()
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }

}