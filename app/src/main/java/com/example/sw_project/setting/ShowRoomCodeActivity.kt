package com.example.sw_project.setting

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sw_project.R
import com.google.android.material.button.MaterialButton

class ShowRoomCodeActivity : AppCompatActivity() {
    private var roomCode: String? = null
    private lateinit var roomCodeTextView: TextView
    private lateinit var copyCodeButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_room_code)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        roomCode = intent.getStringExtra("roomCode")

        setupViews()
        roomCodeTextView.text = roomCode
        setupListeners()
    }

    private fun setupViews() {
        roomCodeTextView = findViewById(R.id.room_code_text)
        copyCodeButton = findViewById(R.id.copy_code_button)
    }

    private fun setupListeners() {
        copyCodeButton.setOnClickListener {
            // 입장 코드 복사 버튼 클릭 시 방 입장 코드 복사
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("room code", roomCode)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "방 입장 코드가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

