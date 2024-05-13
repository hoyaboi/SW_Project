package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import com.example.sw_project.models.com.example.sw_project.adapter.Adapter
import com.example.sw_project.models.com.example.sw_project.adapter.listItem

class RoomMakeActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var Roomlist: ArrayList<listItem>
    private lateinit var listAdapter: Adapter
    private fun saveRoomToDatabase(roomName: String, roomCode: String) {
        val roomData = mapOf(
            "roomName" to roomName,
            "roomCode" to roomCode
        )

        databaseReference.child("rooms").push().setValue(roomData)
            .addOnSuccessListener {
               // 방 생성이 성공한 경우
               Log.d("Room make success","Room_Name: $roomName, Room_Code: $roomCode")
                // 여기에 성공적으로 저장된 후에 할 일 추가
            }
            .addOnFailureListener {
                // 방 생성이 실패한 경우
                Log.e("Room make error", "Failed to create room", it)
                // 실패했을 때의 처리 추가
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_room_make)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        databaseReference = FirebaseDatabase.getInstance().reference
        Roomlist = ArrayList()
        listAdapter = Adapter(Roomlist)

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
                saveRoomToDatabase(RoomName, RoomCode)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "방 이름과 코드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }

            //Log.d("Room make success","Room_Name: $RoomName, Room_Code: $RoomCode")
        }
    }

}