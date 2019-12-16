package com.epuxun.mvvm.main.viewpager

import com.epuxun.mvvm.R
import com.epuxun.mvvm.base.recycler.BaseRecyclerViewAdapter
import com.epuxun.mvvm.base.recycler.BaseViewHolder
import com.epuxun.mvvm.bean.MainBean

class ViewPagerAdapter(resource:Int ,data:List<MainBean>) : BaseRecyclerViewAdapter<MainBean, BaseViewHolder>(resource,data) {

    override fun convert(holder: BaseViewHolder, item: MainBean, position: Int) {
        holder.setText(R.id.tv_msg,item.name)
    }
}