package com.example.sw_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sw_project.databinding.ActivityStartBinding
import com.example.sw_project.models.com.example.sw_project.adapter.Adapter
import com.example.sw_project.models.com.example.sw_project.adapter.listItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.example.sw_project.models.Room

class StartActivity : AppCompatActivity() {
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var binding: ActivityStartBinding
    private lateinit var roomList: ArrayList<listItem>
    private lateinit var listAdapter: Adapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // Firebase 데이터베이스의 "rooms" 레퍼런스 가져오기
        databaseReference = FirebaseDatabase.getInstance().reference.child("rooms")

        // RecyclerView 설정
        roomList = ArrayList()
        listAdapter = Adapter(roomList)
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = listAdapter

        // Firebase Realtime Database에서 방 목록 가져오기
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // dataSnapshot에서 데이터 가져와서 roomList에 추가
                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.child("roomName").getValue(String::class.java)
                    val code = snapshot.child("roomCode").getValue(String::class.java)
                    if (name != null && code != null) {
                        roomList.add(listItem(name, code))
                    }
                }
                // RecyclerView 갱신
                listAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
                Log.e("StartActivity", "Error fetching room list: $databaseError")
            }
        })

        // 버튼 클릭 리스너 등록
        binding.makebutton.setOnClickListener {
            startActivity(Intent(this, RoomMakeActivity::class.java))
        }
        binding.enterbutton.setOnClickListener {
            startActivity(Intent(this, RoomEnterActivity::class.java))
        }

        // RecyclerView 아이템 클릭 리스너 등록
        listAdapter.setItemClickListener(object : Adapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("roomID", roomList[position].code)
                intent.putExtra("roomName", roomList[position].name)
                startActivity(intent)
            }
        })
    }
}
/*class StartActivity : AppCompatActivity() {
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var binding:ActivityStartBinding
    val Roomlist= arrayListOf<listItem>()
    val listAdapter= Adapter(Roomlist)
    lateinit var Roomname:String
    lateinit var Roomcode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityStartBinding.inflate(layoutInflater)
        // enableEdgeToEdge()
        val view=binding.root
        setContentView(view)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        // user email 로깅
        Log.d("StartActivity", "user email: ${user?.email}")

        val makeButton= findViewById<Button>(R.id.makebutton)
        val enterButton=findViewById<Button>(R.id.enterbutton)
        //val recyclerView:RecyclerView=findViewById(R.id.recyclerview)
        //recyclerView.layoutManager=LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)
        //recyclerView.layoutManager=GridLayoutManager(this,2)
        //recyclerView.adapter=listAdapter
        binding.recyclerview.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.recyclerview.adapter= listAdapter
        makeButton.setOnClickListener {
            val intent = Intent(this, RoomMakeActivity::class.java)
            startActivity(intent)

        }
        enterButton.setOnClickListener {
            val intent = Intent(this, RoomEnterActivity::class.java)
            startActivity(intent)
        }
        //원하는 방 선택시 해당방의 index번호(position변수)를 넘겨주며 main화면으로
        listAdapter.setItemClickListener(object: Adapter.OnItemClickListener{
            override fun onClick(v:View,position:Int){
                val intent=Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("roomID", Roomlist[position].code)
                intent.putExtra("roomName", Roomlist[position].name)
                startActivity(intent)
            }
        })
//테스트용
        Roomlist.add(listItem("abc","dhdhdh"))
        Roomlist.add(listItem("dddd","aeijfei"))
        val name: String =intent.getStringExtra("roomname").toString()
        val code: String =intent.getStringExtra("roomcode").toString()
        val check =intent.getIntExtra("check",0)
        if(check==1) {
            Roomlist.add(listItem(name, code))
        }
    }
}*/