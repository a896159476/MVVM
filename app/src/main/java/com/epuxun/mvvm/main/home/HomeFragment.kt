package com.epuxun.mvvm.main.home

import android.view.View
import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.mvvm.BaseMVVMFragment

class HomeFragment : BaseMVVMFragment<HomeViewModel>() {
    override fun getLayoutResID(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): Class<HomeViewModel> {
        return HomeViewModel::class.java
    }

    override fun onViewCreated(view: View) {

    }
}