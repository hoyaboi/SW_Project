package com.example.sw_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PersonalProfileActivity : AppCompatActivity() {
    private var memberUID: String? = null
    private var roomCode: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var profileImageView: ImageView
    private lateinit var profileNameText: TextView
    private lateinit var birthDateText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_profile)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        memberUID = intent.getStringExtra("uID")
        roomCode = intent.getStringExtra("roomCode")
        Log.d("PersonalProfileActivity", "member uID: $memberUID, room ID: $roomCode")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupView()
        loadProfileData()

    }

    private fun setupView() {
        profileImageView = findViewById(R.id.profile_image)
        profileNameText = findViewById(R.id.profile_name_text)
        birthDateText = findViewById(R.id.birthday_text)
    }

    private fun loadProfileData() {
        // memberUID로 프로필 정보 가져오기
        // rooms -> participants -> 이름, 프로필 이미지 가져오기
        // users -> uID -> 생일 가져오기


    }
}
