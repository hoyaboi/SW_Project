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
import com.google.firebase.database.*
import android.widget.Toast
import com.example.sw_project.models.Room

class roomenterActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference

    private fun enterRoom(roomName: String, roomCode: String) {
        val query: Query = databaseReference.child("rooms").orderByChild("roomName").equalTo(roomName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 방이 존재하는 경우
                    for (snapshot in dataSnapshot.children) {
                        val room = snapshot.getValue(Room::class.java)
                        if (room?.roomCode == roomCode) {
                            // 입력한 코드가 일치하는 경우
                            // 입장할 방으로 이동하는 코드 추가
                            val intent = Intent(this@roomenterActivity, MainActivity::class.java)
                            intent.putExtra("roomId", room.roomId)
                            startActivity(intent)
                            finish()
                            return
                        }
                    }
                    // 일치하는 코드가 없는 경우
                    Toast.makeText(this@roomenterActivity, "잘못된 코드입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 해당하는 이름의 방이 없는 경우
                    Toast.makeText(this@roomenterActivity, "존재하지 않는 방 이름입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터베이스 쿼리가 취소된 경우
                Log.e("roomenterActivity", "Error querying room: $databaseError")
                Toast.makeText(this@roomenterActivity, "방을 찾는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_roomenter)
       /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        databaseReference = FirebaseDatabase.getInstance().reference

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
            enterRoom(RoomName, RoomCode)
            if (RoomName.isNotEmpty() && RoomCode.isNotEmpty()) {

            } else {
                Toast.makeText(this, "방 이름과 코드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
           // if(RoomName.isNotEmpty() && RoomCode.isNotEmpty()){
                //이름과 코드가 같은 방을 찾아 목록에 추가, 없으면 틀리다고 메세지
                //후에 방목록에 대한 정보가 생기면 구현할 예정
                //val intent=Intent(this, StartActivity::class.java)
                //intent.putExtra("roomname",RoomName)
                //intent.putExtra("roomcode",RoomCode)
                //intent.putExtra("check",1)
                //startActivity(intent)
                //finish()
              //  enterRoom(RoomName, RoomCode)
           // }

           // Log.d("Room make success","Room_Name: $RoomName, Room_Code: $RoomCode")
        }
    }
}