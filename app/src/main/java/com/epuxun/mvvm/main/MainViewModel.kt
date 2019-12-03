package com.epuxun.mvvm.main

import android.system.Os.socket
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epuxun.mvvm.utli.socket.SocketHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.Socket


class MainViewModel : ViewModel() {

    val apkUrl = MutableLiveData<String>()

    private val repository by lazy {
        MainRepository()
    }

    init {
        versionUpdateDetection()
    }

    /**
     * 版本更新检测
     */
    private fun versionUpdateDetection() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.queryVersion()?.let {
                launch(Dispatchers.Main) {
                    apkUrl.value = it
                }
            }
        }
    }

    fun socketHelper(){
        viewModelScope.launch(Dispatchers.IO) {
            SocketHelper.initSocket()

            while (isActive){
                val connectionSucceeded = async {
                    if (SocketHelper.socket.isClosed){
                        return@async SocketHelper.initSocket()
                    }
                    return@async true
                }.await()
                if (connectionSucceeded){
                    val text = SocketHelper.socketReadText()
                }
            }
        }
    }
}
