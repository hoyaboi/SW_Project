package com.example.sw_project

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.content.Intent
import android.util.Log
import android.widget.GridView
import android.view.View
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class StartActivity : AppCompatActivity() {
    private var user = FirebaseAuth.getInstance().currentUser
    private lateinit var binding:ActivityMainBinding
    val Roomlist= arrayListOf<listItem>()
    val listAdapter=Adapter(Roomlist)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        // enableEdgeToEdge()
        setContentView(binding.root)

        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        // user email 로깅
        Log.d("StartActivity", "user email: ${user?.email}")

        val makeButton= findViewById<Button>(R.id.makebutton)
        val enterButton=findViewById<Button>(R.id.enterbutton)
        val recyclerView:RecyclerView=findViewById(R.id.recyclerview)
        //recyclerView.layoutManager=LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager=GridLayoutManager(this,2)
        recyclerView.adapter=listAdapter

        makeButton.setOnClickListener {
            val intent = Intent(this, roommakeActivity::class.java)
            startActivity(intent)
        }
        enterButton.setOnClickListener {
            val intent = Intent(this, roomenterActivity::class.java)
            startActivity(intent)
        }
        listAdapter.setItemClickListener(object:Adapter.OnItemClickListener{
            override fun onClick(v:View,position:Int){
                val intent=Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("roomID", position)
                startActivity(intent)
            }
        })

        Roomlist.add(listItem("abc","dhdhdh"))
        Roomlist.add(listItem("dddd","aeijfei"))
    }
}