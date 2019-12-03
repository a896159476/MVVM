package com.epuxun.mvvm.main

import com.epuxun.mvvm.utli.socket.SocketHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainRepository {

    /**
     * 查询是否是最新版本，不是则返回apk地址
     */
    suspend fun queryVersion(): String? {
        //模拟网络请求
        delay(500L)
//        return "https://raw.githubusercontent.com/xuexiangjys/XUpdate/master/apk/xupdate_demo_1.0.2.apk"
        return null
    }

}