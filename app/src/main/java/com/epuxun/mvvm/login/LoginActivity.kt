package com.epuxun.mvvm.login

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.mvvm.BaseMVVMActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseMVVMActivity<LoginViewModel>(){

    override fun getLayoutResID(): Int {
        return R.layout.activity_login
    }

    override fun addObserver(): LifecycleObserver? {
        return null
    }

    override fun getViewModel(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }

    override fun onCreate() {
        viewModel.userAccount.observe(this, Observer {
            viewModel.getCode()
        })
        viewModel.code.observe(this, Observer {
            if (viewModel.login()){
                finish()
            }
        })

        setEditTextMutableLiveData(et_userAccount,viewModel.userAccount)
        setEditTextMutableLiveData(et_code,viewModel.code)
    }

}