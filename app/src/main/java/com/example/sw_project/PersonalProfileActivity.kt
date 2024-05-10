package com.example.sw_project

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonalProfileActivity : AppCompatActivity() {
    private var profileName: String? = null
    private var roomID: Int = 0

    private lateinit var profileNameText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_profile)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        profileName = intent.getStringExtra("profileName")
        roomID = intent.getIntExtra("roomID", 0)

        setupView()

        profileNameText.text = "${profileName}'s Profile"
    }

    private fun setupView() {
        profileNameText = findViewById(R.id.tmp_string)
    }
}
