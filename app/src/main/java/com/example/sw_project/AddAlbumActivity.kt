package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw_project.tabbar.AlbumFragment
import com.google.android.material.button.MaterialButton

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
            if (albumname.isNotEmpty()) {
                val intent = Intent(this, AlbumFragment::class.java)
                intent.putExtra("albumname", albumname)
                intent.putExtra("check", 1)
                finish()
            }
        }
    }
}