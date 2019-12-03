package com.epuxun.mvvm.permissions

import android.Manifest
import android.content.Intent
import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.permissions.BasePermissionsActivity
import com.epuxun.mvvm.main.MainActivity

class PermissionsActivity : BasePermissionsActivity() {

    override fun getLayoutResID(): Int {
        return R.layout.activity_permissions
    }

    override fun permissionsList(): Array<String> {
        return arrayOf(Manifest.permission.CAMERA)
    }

    override fun successCallback() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}