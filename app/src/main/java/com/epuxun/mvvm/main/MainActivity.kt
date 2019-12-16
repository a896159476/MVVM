package com.epuxun.mvvm.main

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.mvvm.BaseMVVMActivity
import com.epuxun.mvvm.bean.MainBean
import com.epuxun.mvvm.main.viewpager.MainFragmentAdapter
import com.epuxun.mvvm.main.viewpager.ViewPagerAdapter
import com.epuxun.mvvm.utli.PERMISSION_REQUEST_CODE
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*


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
        viewPager2.adapter = MainFragmentAdapter(this)
        viewPager2.setPageTransformer { page, position ->
            page.run {
                //绝对值:百分比
                val absPos = Math.abs(position)
                //左边距 = 位置百分比 * 最大距离
                translationX = absPos * 350f
                translationY = absPos * 500f
                //缩放
                val scale = if (absPos > 1) 0F else 1 - absPos
                scaleX = scale
                scaleY = scale
                //旋转角度 = 百分比 * 最大角度
                rotation = position * 360
            }
        }

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if (position == 0) {
                tab.text = "首页"
            } else {
                tab.text = "我的"
            }
        }.attach()

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