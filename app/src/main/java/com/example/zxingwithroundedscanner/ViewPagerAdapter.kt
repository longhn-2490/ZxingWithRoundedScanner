package com.example.zxingwithroundedscanner

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity, val fragments: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2 // Number of fragments
    }
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragments[position]
            1 -> fragments[position]
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}