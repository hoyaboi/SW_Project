package com.example.sw_project

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.sw_project.databinding.ActivityFullscreenPhotoBinding

class FullScreenPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUri = intent.getParcelableExtra<Uri>("photoUri")
        if (photoUri != null) {
            Glide.with(this).load(photoUri).into(binding.fullscreenImageView)
        } else {
            finish() // If no photo URI was provided, close the activity
        }
    }
}
