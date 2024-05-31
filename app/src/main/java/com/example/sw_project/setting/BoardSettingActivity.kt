package com.example.sw_project.setting

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.R
import com.example.sw_project.models.com.example.sw_project.adapter.MyBoardAdapter
import com.example.sw_project.models.com.example.sw_project.adapter.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class BoardSettingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var noDataText: TextView
    private var roomCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_setting)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        roomCode = intent.getStringExtra("roomCode") ?: return

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        noDataText = findViewById(R.id.no_data_text)
        loadBoard()

    }

    private fun loadBoard() {
        // 게시물 데이터를 담을 Post 타입 리스트
        val postsList = mutableListOf<Post>()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

        // 리사이클러 뷰 레이아웃 설정
        val recyclerView: RecyclerView = findViewById(R.id.my_board_container)
        recyclerView.layoutManager = GridLayoutManager(this, 3)  // 3열 그리드
        recyclerView.adapter = MyBoardAdapter(postsList, this)

        // 게시물 로드
        database.child("posts").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                postsList.clear()
                for (postSnapshot in snapshot.children) {
                    val curRoomCode = postSnapshot.child("roomCode").getValue(String::class.java)
                    val curUID = postSnapshot.child("uid").getValue(String::class.java)

                    if (curRoomCode == roomCode && curUID == auth.currentUser!!.uid) {
                        val imageUri = postSnapshot.child("imageUri").getValue(String::class.java) ?: ""
                        val content = postSnapshot.child("content").getValue(String::class.java) ?: ""
                        val postID = postSnapshot.child("postId").getValue(String::class.java) ?: ""
                        val postTime = postSnapshot.child("postTime").getValue(String::class.java) ?: ""

                        Log.d("BoardSettingActivity", "Loaded post: imageUri = $imageUri, content = $content")

                        try {
                            val date = dateFormat.parse(postTime)
                            postsList.add(Post(imageUri, content, postID, date))
                        } catch (e: ParseException) {
                            Log.e("BoardSettingActivity", "Error parsing date", e)
                        }
                    }
                }
                noDataText.visibility = if (postsList.isEmpty()) View.VISIBLE else View.GONE
                postsList.sortByDescending { it.date }
                recyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("BoardSettingActivity", "loadBoard:onCancelled", error.toException())
            }
        })
    }

}
