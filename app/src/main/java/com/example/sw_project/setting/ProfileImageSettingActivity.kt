package com.example.sw_project.setting

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.sw_project.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileImageSettingActivity : AppCompatActivity() {
    private var profileUri: String? = null
    private var roomCode: String? = null

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
        roomCode = intent.getStringExtra("roomCode") ?: return

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
                profileUri = uri.toString()
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
            profileUri = ""
        }

        changeButton.setOnClickListener {
            if (!profileUri.isNullOrEmpty()) {
                uploadImageToStorage(Uri.parse(profileUri))
            } else {
                updateProfileUriInDatabase("")
            }
        }
    }

    private fun uploadImageToStorage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val userID = auth.currentUser!!.uid
        val imageRef = storageRef.child("images/rooms/$roomCode/$userID/profile.jpg")

        // 이미지 로드 및 리사이즈
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .override(256, 256) // 조정할 이미지 크기 지정
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    // 비트맵을 바이트 배열로 변환
                    val baos = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                    val data = baos.toByteArray()

                    // Firebase에 업로드
                    imageRef.putBytes(data)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { uri ->
                                updateProfileUriInDatabase(uri.toString())
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ProfileImageSettingActivity, "프로필 이미지 변경에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_LONG).show()
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun updateProfileUriInDatabase(uri: String) {
        val userID = auth.currentUser!!.uid
        database.child("rooms").child(roomCode!!).child("participants").child(userID).child("profileUri")
            .setValue(uri)
            .addOnSuccessListener {
                Toast.makeText(this, "프로필 이미지가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "프로필 이미지 변경에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
            }
    }

}