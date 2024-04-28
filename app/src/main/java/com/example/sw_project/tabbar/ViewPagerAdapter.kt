package com.example.sw_project.tabbar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    private val NUM_PAGES = 4
    override fun getItemCount(): Int = NUM_PAGES  // 총 페이지 수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BoardFragment()
            1 -> AlbumFragment()
            2 -> ScheduleFragment()
            3 -> ProfileFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
