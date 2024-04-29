package com.example.sw_project

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        roomID = intent.getIntExtra("roomID", 0)
        // user email, room id 로깅
        Log.d("StartPage", "user email: ${user?.email}, room ID: $roomID")

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
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }

        viewPager.adapter = ViewPagerAdapter(
            this,
            FirebaseAuth.getInstance().currentUser,
            intent.getIntExtra("roomID", 0)
        )

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

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            return
        } else {
            Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}