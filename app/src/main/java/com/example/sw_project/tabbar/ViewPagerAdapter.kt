package com.example.sw_project.tabbar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.auth.FirebaseUser

class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val user: FirebaseUser?,
    private val roomID: Int
) : FragmentStateAdapter(fragmentActivity) {
    private val NUM_PAGES = 4
    override fun getItemCount(): Int = NUM_PAGES  // 총 페이지 수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BoardFragment.newInstance(user?.email, roomID)
            1 -> AlbumFragment.newInstance(user?.email, roomID)  // AlbumFragment도 같은 방식으로
            2 -> ScheduleFragment.newInstance(user?.email, roomID)  // ScheduleFragment도 같은 방식으로
            3 -> ProfileFragment.newInstance(user?.email, roomID)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
