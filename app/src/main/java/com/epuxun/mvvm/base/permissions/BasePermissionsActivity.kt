package com.epuxun.mvvm.base.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

abstract class BasePermissionsActivity : AppCompatActivity() {

    private val permissionsRequestCode = 100

    //判断是否需要检测，防止不停的弹框
    private var isNeedCheck = true

    private lateinit var permissionsDialog: PermissionsDialog

    abstract fun getLayoutResID(): Int

    //需要授权的数组
    abstract fun permissionsList(): Array<String>

    //授权成功回调
    abstract fun successCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        //防止截屏攻击风险
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        //仿系统自带短信界面 可以完全漂浮在软键盘之上
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onCreate(savedInstanceState)

        setContentView(getLayoutResID())

        permissionsDialog = PermissionsDialog()
    }

    /**
     * 申请权限
     */
    override fun onResume() {
        super.onResume()
        if (isNeedCheck) {
            initRequestPermission(permissionsList())
        } else {
            isNeedCheck = true
        }
    }

    /**
     * 公共方法:初始化时统一申请权限
     */
    private fun initRequestPermission(@NonNull permissions: Array<String>) {
        val needRequestPermissionList = ArrayList<String>()
        for (permission in permissions) {
            //如果没有授权放入待授权集合
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needRequestPermissionList.add(permission)
            }
        }
        //将待授权集合转换为数组并申请授权
        val array = needRequestPermissionList.toTypedArray()
        if (array.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, array, permissionsRequestCode)
        } else {
            successCallback()
        }
    }

    /**
     * 授权回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionsRequestCode) {
            verifyPermissions(grantResults)
        }
    }

    /**
     * 判断授权是否成功
     */
    private fun verifyPermissions(grantResults: IntArray) {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                showDialog()
                return
            }
        }
        successCallback()
    }

    /**
     * 拒接授权弹出提示框
     */
    private fun showDialog() {
        permissionsDialog.show(supportFragmentManager, localClassName)
        isNeedCheck = false
    }

}