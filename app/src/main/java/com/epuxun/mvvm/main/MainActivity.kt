package com.epuxun.mvvm.main

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.mvvm.BaseMVVMActivity
import com.epuxun.mvvm.utli.PERMISSION_REQUEST_CODE


class MainActivity : BaseMVVMActivity<MainViewModel>() {

    override fun getLayoutResID(): Int {
        return R.layout.activity_main
    }

    override fun addObserver(): LifecycleObserver? {
        return MainLifecycleObserver()
    }

    override fun getViewModel(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    private val receive = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                //安装
                if (it.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    installation()
                }
            }
        }
    }

    override fun onCreate() {
        //注册广播
        registerExternalReceiver(receive, DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        //应用更新提示
        viewModel.apkUrl.observe(this, Observer {
            showInstallationDialog(it, PERMISSION_REQUEST_CODE)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //8.0以上安装需要授权
        if (requestCode == PERMISSION_REQUEST_CODE) {
            viewModel.apkUrl.value?.let {
                showInstallationDialog(it, PERMISSION_REQUEST_CODE)
            }
        }
    }

}