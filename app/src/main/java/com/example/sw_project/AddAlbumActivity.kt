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

class AddAlbumActivity : AppCompatActivity() {

    private lateinit var makeButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_album)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        makeButton=findViewById<MaterialButton>(R.id.create_button)
        makeButton.setOnClickListener {

            val editalbumname = findViewById<EditText>(R.id.Albumname)
            val albumname = editalbumname.getText().toString()
            Log.d("AddAlbum","albumname: ${albumname}")
            //테스트용 입력값 fragment로 보내기
            if (albumname.isNotEmpty()) {
                saveAlbumToDatabase(albumname)
                /*val intent = Intent(this, AlbumFragment::class.java)
                intent.putExtra("albumname", albumname)
                intent.putExtra("check", 1)
                finish()*/
                //val bundle=Bundle()
                //bundle.putString("albumname",albumname)
                //bundle.putInt("check",1)
                //finish()
            }
            else {
                Toast.makeText(this, "앨범 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveAlbumToDatabase(albumName: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("albums")
        val albumId = databaseReference.push().key ?: return

        val albumData = mapOf(
            "id" to albumId,
            "name" to albumName,
            "coverImageUrl" to "" // 추후 앨범 커버 이미지를 추가할 수 있습니다.
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