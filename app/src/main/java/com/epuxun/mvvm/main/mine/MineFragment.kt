package com.epuxun.mvvm.main.mine

import android.view.View
import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.mvvm.BaseMVVMFragment

class MineFragment : BaseMVVMFragment<MineViewModel>() {
    override fun getLayoutResID(): Int {
        return R.layout.fragment_mine
    }

    override fun getViewModel(): Class<MineViewModel> {
        return MineViewModel::class.java
    }

    override fun onViewCreated(view: View) {

    }
}