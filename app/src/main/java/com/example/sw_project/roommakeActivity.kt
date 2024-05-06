package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EdgeEffect
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.util.Log

class roommakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_roommake)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        val cancelButton= findViewById<Button>(R.id.cancelbutton)
        val checkButton=findViewById<Button>(R.id.checkbutton)
        cancelButton.setOnClickListener {
            finish()
        }
        checkButton.setOnClickListener{
//확인 버튼 누를 시 입력 받은 방 이름과 코드 저장
            val editRoomName=findViewById<EditText>(R.id.Roomname)
            val editRoomCode=findViewById<EditText>(R.id.Roomcode)
            val RoomName=editRoomName.getText().toString()
            val RoomCode=editRoomCode.getText().toString()
            if(RoomName.isNotEmpty() && RoomCode.isNotEmpty()){
                //이름과 코드가 같은 방이 없을 경우 방 생성
                val intent=Intent(this, StartActivity::class.java)
                intent.putExtra("roomname",RoomName)
                intent.putExtra("roomcode",RoomCode)
                intent.putExtra("check",1)
                startActivity(intent)
            }

            Log.d("Room make success","Room_Name: $RoomName, Room_Code: $RoomCode")
        }
    }
}