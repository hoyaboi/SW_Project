package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.sw_project.models.Room
import com.example.sw_project.models.com.example.sw_project.adapter.Adapter
import com.example.sw_project.models.com.example.sw_project.adapter.listItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RoomMakeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var Roomlist: ArrayList<listItem>
    private lateinit var listAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_room_make)

        auth = FirebaseAuth.getInstance()
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
            val roomName=editRoomName.getText().toString()
            val roomCode=editRoomCode.getText().toString()
            if(roomName.isNotEmpty() && roomCode.isNotEmpty()){
                //이름과 코드가 같은 방이 없을 경우 방 생성
                saveRoomToDatabase(roomName, roomCode)
            }
            else {
                Toast.makeText(this, "방 이름과 코드를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRoomToDatabase(roomName: String, roomCode: String) {
        val userUID = auth.currentUser!!.uid // 사용자 UID
        val participantDetails = hashMapOf(
            "uID" to userUID,
            "profileUri" to ""
        )
        val participants = hashMapOf(
            userUID to participantDetails
        )
        val roomData = Room(
            roomCode = roomCode,
            roomName = roomName,
            participants = participants
        )

        // 방 이름과 Code로 데이터베이스를 검색
        val roomsQuery = databaseReference.child("rooms").orderByChild("roomCode").equalTo(roomCode)

        roomsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 이미 방이 존재하는 경우
                    Toast.makeText(this@RoomMakeActivity, "이미 존재하는 방 코드입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    databaseReference.child("rooms").push().setValue(roomData)
                        .addOnSuccessListener {
                            Log.d("Room make success", "Room_Name: $roomName, Room_Code: $roomCode")
                            val intent = Intent(this@RoomMakeActivity, MainActivity::class.java)
                            intent.putExtra("roomCode", roomCode)
                            intent.putExtra("roomName", roomName)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Room make error", "Failed to create room", exception)
                            Toast.makeText(this@RoomMakeActivity, "방 생성에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Room make error", "Error checking if room exists: $databaseError")
            }
        })
    }
}