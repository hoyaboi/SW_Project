package com.example.sw_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonalProfileActivity : AppCompatActivity() {
    private var memberName: String? = null
    private var roomCode: String? = null

    private lateinit var profileNameText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_profile)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        memberName = intent.getStringExtra("memberName")
        roomCode = intent.getStringExtra("roomCode")
        Log.d("PersonalProfileActivity", "name: $memberName, room ID: $roomCode")

        setupView()
        setupListeners()

        profileNameText.text = "${memberName}'s Profile"
    }

    private fun setupView() {
        profileNameText = findViewById(R.id.tmp_string)
    }

    private fun setupListeners() {

    }
}
