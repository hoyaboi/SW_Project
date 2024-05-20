package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.database.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.sw_project.models.Room
import com.google.firebase.auth.FirebaseAuth

class RoomEnterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_room_enter)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val cancelButton= findViewById<Button>(R.id.cancelbutton)
        val checkButton=findViewById<Button>(R.id.checkbutton)
        cancelButton.setOnClickListener {
            finish()
        }
        checkButton.setOnClickListener{
            //확인 버튼 누를 시 입력 받은 방 이름과 코드 저장
            val editRoomName = findViewById<EditText>(R.id.Roomname)
            val editRoomCode = findViewById<EditText>(R.id.Roomcode)
            val roomName = editRoomName.getText().toString()
            val roomCode = editRoomCode.getText().toString()
            if (roomName.isNotEmpty() && roomCode.isNotEmpty()) {
                enterRoom(roomName, roomCode)
            } else {
                Toast.makeText(this, "방 이름과 코드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun enterRoom(roomName: String, roomCode: String) {
        val query: Query = databaseReference.child("rooms").orderByChild("roomName").equalTo(roomName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 방이 존재하는 경우
                    for (snapshot in dataSnapshot.children) {
                        val room = snapshot.getValue(Room::class.java)
                        if (room?.roomCode == roomCode) { // 입력한 코드가 일치하는 경우
                            // 참가자 추가(이미 존재하면 경고 메시지)
                            addParticipants(roomName, roomCode)
                            return
                        }
                    }
                    // 일치하는 코드가 없는 경우
                    Toast.makeText(this@RoomEnterActivity, "잘못된 코드입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 해당하는 이름의 방이 없는 경우
                    Toast.makeText(this@RoomEnterActivity, "존재하지 않는 방 이름입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터베이스 쿼리가 취소된 경우
                Log.e("roomenterActivity", "Error querying room: $databaseError")
                Toast.makeText(this@RoomEnterActivity, "방을 찾는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addParticipants(roomName: String, roomCode: String) {
        val query: Query = databaseReference.child("rooms").orderByChild("roomName").equalTo(roomName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var roomFound = false
                for (snapshot in dataSnapshot.children) {
                    val room = snapshot.getValue(Room::class.java)
                    if (room != null && room.roomCode == roomCode) {
                        roomFound = true
                        val participantsRef = snapshot.child("participants").ref
                        val userID = auth.currentUser!!.uid

                        participantsRef.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(participantSnapshot: DataSnapshot) {
                                if (!participantSnapshot.exists()) {
                                    // 참가자 목록에 사용자가 없는 경우, 참가자 추가
                                    participantsRef.child(userID).setValue(mapOf("uID" to userID, "profileUri" to "")).addOnSuccessListener {
                                        Log.d("RoomEnterActivity", "Participant added successfully.")
                                        navigateToRoom(room.roomCode, room.roomName)
                                    }.addOnFailureListener { exception ->
                                        Log.e("RoomEnterActivity", "Failed to add participant", exception)
                                    }
                                } else {
                                    Toast.makeText(this@RoomEnterActivity, "이미 참여 중인 방입니다.", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("RoomEnterActivity", "Error checking participant: $error")
                            }
                        })
                        break
                    }
                }
                if (!roomFound) {
                    Toast.makeText(this@RoomEnterActivity, "일치하는 방이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("RoomEnterActivity", "Error querying room: $databaseError")
                Toast.makeText(this@RoomEnterActivity, "방을 찾는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToRoom(roomCode: String?, roomName: String?) {
        val intent = Intent(this@RoomEnterActivity, MainActivity::class.java)
        intent.putExtra("roomCode", roomCode)
        intent.putExtra("roomName", roomName)
        startActivity(intent)
        finish()
    }


}