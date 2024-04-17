package com.example.sw_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
import android.widget.GridView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class StartActivity : AppCompatActivity() {

    val datalist= arrayListOf(
        RoomList("1번방","abcde"),RoomList("2번방","sh6847"),RoomList("2번방 어디갔어","dkdkdk")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        val makeButton= findViewById<Button>(R.id.makebutton)
        val enterButton=findViewById<Button>(R.id.enterbutton)
        val recyclerView:RecyclerView=findViewById(R.id.recyclerview)
        //recyclerView.layoutManager=LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager=GridLayoutManager(this,2)
        recyclerView.adapter=Adapter(datalist)

        makeButton.setOnClickListener {
            val intent = Intent(this, roommakeActivity::class.java)
            startActivity(intent)
        }
        enterButton.setOnClickListener {
            val intent = Intent(this, roomenterActivity::class.java)
            startActivity(intent)
        }
    }
}