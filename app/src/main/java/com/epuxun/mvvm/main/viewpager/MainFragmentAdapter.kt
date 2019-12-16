package com.epuxun.mvvm.main.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.epuxun.mvvm.main.home.HomeFragment
import com.epuxun.mvvm.main.mine.MineFragment

class MainFragmentAdapter(fa:FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                HomeFragment()
            } else -> {
                MineFragment()
            }
        }
    }
}