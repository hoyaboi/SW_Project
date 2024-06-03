package com.example.sw_project

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.sw_project.databinding.ActivityFullscreenPhotoBinding

class FullScreenPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val photoUri = intent.getParcelableExtra<Uri>("photoUri")
        if (photoUri != null) {
            Glide.with(this).load(photoUri).into(binding.fullscreenImageView)
        } else {
            finish() // If no photo URI was provided, close the activity
        }
    }
}
