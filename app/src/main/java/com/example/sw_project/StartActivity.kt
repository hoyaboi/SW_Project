package com.example.sw_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sw_project.databinding.ActivityStartBinding
import com.example.sw_project.models.com.example.sw_project.adapter.Adapter
import com.example.sw_project.models.com.example.sw_project.adapter.listItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class StartActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivityStartBinding
    private val roomList = arrayListOf<listItem>()
    private lateinit var listAdapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Set up RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        listAdapter = Adapter(roomList)
        binding.recyclerview.adapter = listAdapter
        setupListeners()

        // Load rooms
        loadRooms()
    }

    private fun setupListeners() {
        binding.makebutton.setOnClickListener {
            val intent = Intent(this, RoomMakeActivity::class.java)
            startActivity(intent)
        }
        binding.enterbutton.setOnClickListener {
            val intent = Intent(this, RoomEnterActivity::class.java)
            startActivity(intent)
        }
        listAdapter.setItemClickListener(object : Adapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("roomID", roomList[position].id)
                intent.putExtra("roomName", roomList[position].name)
                startActivity(intent)
            }
        })
    }

    private fun loadRooms() {
        val userID = auth.currentUser?.uid
        if (userID == null) {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            return
        }
        databaseReference.child("rooms").orderByChild("participants/$userID/uID").equalTo(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach { roomSnapshot ->
                            val roomName = roomSnapshot.child("roomName").getValue(String::class.java)
                            val roomID = roomSnapshot.key
                            if (roomName != null && roomID != null) {
                                roomList.add(listItem(roomName, roomID))
                                listAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("StartActivity", "Error loading rooms: $error")
                }
            })
    }
}