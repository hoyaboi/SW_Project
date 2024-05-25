package com.example.sw_project

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PersonalProfileActivity : AppCompatActivity() {
    private var memberUID: String? = null
    private var roomCode: String? = null
    private var profileUri: String? = null
    private var memberName: String? = null
    private var memberBirthDate: String? = null

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
        // 프로필 이미지 로딩
        database.child("rooms").child(roomCode!!).child("participants").child(memberUID!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        profileUri = snapshot.child("profileUri").getValue(String::class.java) ?: ""
                        Glide.with(this@PersonalProfileActivity)
                            .load(profileUri!!.ifEmpty { R.drawable.tmp_face })  // 빈 URI인 경우 기본 이미지 사용
                            .circleCrop()
                            .into(profileImageView)

                        // 프로필 정보 로딩
                        loadUserInfo()
                    } else {
                        profileNameText.text = "해당 방에 참여중인 인원이 아닙니다."
                        birthDateText.text = ""
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("PersonalProfileActivy", "Fail to load profile info: $error")
                    finish()
                }
            })
    }

    private fun loadUserInfo() {
        database.child("users").child(memberUID!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                memberName = snapshot.child("name").getValue(String::class.java)
                memberBirthDate = snapshot.child("birthDate").getValue(String::class.java)

                if (memberName != null && memberBirthDate != null) {
                    // 날짜 형식 변환
                    val inputFormat = SimpleDateFormat("yyyy/M/d", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                    val date = inputFormat.parse(memberBirthDate.toString())
                    val formattedDate = outputFormat.format(date ?: Date())

                    profileNameText.text = "이름 : $memberName"
                    birthDateText.text = "생일 : $formattedDate"
                } else {
                    profileNameText.text = "프로필 로딩에 문제가 발생했습니다."
                    birthDateText.text = ""
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("PersonalProfileActivy", "Fail to load profile info: $error")
                finish()
            }
        })
    }
}
