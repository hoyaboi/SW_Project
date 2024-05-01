package com.example.sw_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddBoardActivity : AppCompatActivity() {
    private var userEmail: String? = null
    private var roomID: Int = 0

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_board)

        setupPermissions()
        setupImagePickerLauncher()

        // 이전 프래그먼트로부터 데이터 받기
        userEmail = intent.getStringExtra("userEmail")
        roomID = intent.getIntExtra("roomID", 0)

        // 이미지 추가 버튼 클릭 시
        findViewById<FloatingActionButton>(R.id.add_image_button).setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        // 이미지 변경 버튼 클릭 시
        findViewById<MaterialButton>(R.id.change_image_button).setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        // 이미지 제거 버튼 클릭 시
        findViewById<MaterialButton>(R.id.remove_image_button).setOnClickListener {
            findViewById<ImageView>(R.id.selected_image).setImageDrawable(null)
            findViewById<LinearLayout>(R.id.image_actions_container).visibility = View.GONE
            findViewById<LinearLayout>(R.id.add_image_container).visibility = View.VISIBLE
        }

    }

    private fun setupPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }

    private fun setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                findViewById<ImageView>(R.id.selected_image).setImageURI(uri)
                findViewById<LinearLayout>(R.id.image_actions_container).visibility = View.VISIBLE
                findViewById<LinearLayout>(R.id.add_image_container).visibility = View.GONE
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
        }
    }
}
