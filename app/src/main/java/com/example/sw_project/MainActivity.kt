package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sw_project.databinding.ActivityMainBinding
import com.example.sw_project.tabbar.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var user = FirebaseAuth.getInstance().currentUser
    private var roomID = 0
    // 데이터베이스에서 user email과 room id가 일치하는 데이터를 가져오면 됩니다.

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager = findViewById<ViewPager2>(R.id.viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        val tabIcon = listOf(
            R.drawable.board,
            R.drawable.album,
            R.drawable.schedule,
            R.drawable.profile
        )
        val backButton = findViewById<AppCompatButton>(R.id.backButton)

        backButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewPager.adapter = ViewPagerAdapter(this)

        // TabLayout과 ViewPager2 연동
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "게시판"
                1 -> "앨범"
                2 -> "일정"
                3 -> "프로필"
                else -> null
            }
            tab.setIcon(tabIcon[position])
        }.attach()

        roomID = intent.getIntExtra("roomID", 0)
        // user email, room id 로깅
        Log.d("StartPage", "user email: ${user?.email}, room ID: $roomID")

    }
}