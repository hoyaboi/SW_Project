package com.example.sw_project.setting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.sw_project.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileImageSettingActivity : AppCompatActivity() {
    private var profileUri: String? = null
    private var roomID: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var profileImage: ImageView
    private lateinit var changeProfileButton: MaterialButton
    private lateinit var removeProfileButton: MaterialButton
    private lateinit var changeButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_image_setting)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        profileUri = intent.getStringExtra("profileUri") ?: return
        roomID = intent.getStringExtra("roomID") ?: return

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupViews()
        loadProfileImage()
        setupPermissions()
        setupImagePickerLauncher()
        setupListeners()
    }

    private fun setupViews() {
        profileImage = findViewById(R.id.profile_image)
        changeProfileButton = findViewById(R.id.change_image_button)
        removeProfileButton = findViewById(R.id.remove_image_button)
        changeButton = findViewById(R.id.change_button)
    }

    private fun loadProfileImage() {
        if(!profileUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileUri)
                .circleCrop()
                .into(profileImage)
        } else {
            Glide.with(this)
                .load(R.drawable.tmp_face)
                .circleCrop()
                .into(profileImage)
        }
    }

    private fun setupPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 스토리지 읽기 권한 요청
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }

    private fun setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(profileImage)
            }
        }
    }

    private fun setupListeners() {
        changeProfileButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        removeProfileButton.setOnClickListener {
            Glide.with(this)
                .load(R.drawable.tmp_face)
                .circleCrop()
                .into(profileImage)
        }

        changeButton.setOnClickListener {
            // 프로필 이미지를 firebase storage에 저장 후, 데이터베이스에 uri 저장
            // 데이터베이스 rooms의 현재 roomID와 일치하는 participants의 profileUri 수정

            finish()
        }
    }

}