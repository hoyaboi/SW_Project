package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw_project.tabbar.AlbumFragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import android.widget.Toast
import com.example.sw_project.databinding.ActivityAddAlbumBinding

class AddAlbumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAlbumBinding
    private lateinit var databaseReference: DatabaseReference
    private var roomCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        roomCode = intent.getStringExtra("roomID")

        databaseReference = FirebaseDatabase.getInstance().getReference("albums")

        binding.createButton.setOnClickListener {
            val albumName = binding.Albumname.text.toString().trim()

            if (albumName.isNotEmpty()) {
                saveAlbumToDatabase(albumName)
            } else {
                Toast.makeText(this, "앨범 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAlbumToDatabase(albumName: String) {
        val albumId = databaseReference.push().key ?: return

        val albumData = mapOf(
            "id" to albumId,
            "name" to albumName,
            //"coverImageUrl" to "", // 추후 앨범 커버 이미지를 추가할 수 있습니다.
            "roomCode" to roomCode // roomCode를 추가하여 특정 방에 해당하는 앨범을 저장
        )

        databaseReference.child(albumId).setValue(albumData)
            .addOnSuccessListener {
                Toast.makeText(this, "앨범이 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "앨범 추가에 실패했습니다: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}