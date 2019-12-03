package com.epuxun.mvvm.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.epuxun.mvvm.utli.showLongToast

class LoginViewModel : ViewModel() {

    //用户账号
    var userAccount = MutableLiveData<String>()
    //验证码
    var code = MutableLiveData<String>()

    init {
        userAccount.value = ""
        code.value = ""
    }

    /**
     * 获取验证码
     */
    fun getCode(){
        if (userAccount.value.equals("13800000001")){
            showLongToast("获取到验证码")
        }
    }

    /**
     * 登陆
     */
    fun login():Boolean{
        if (userAccount.value.equals("13800000001") && code.value.equals("123456")){
            return true
        }
        return false
    }
}