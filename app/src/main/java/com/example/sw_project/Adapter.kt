package com.example.sw_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
data class RoomList(var name: String, var code: String)

class RoomAdapterHolder(v:View):RecyclerView.ViewHolder(v){
    var nameView:TextView=v.findViewById(R.id.roomView_name)
    var codeView:TextView=v.findViewById(R.id.roomView_code)
    val name=nameView
    val code=codeView
}

class RoomAdapter(val datalist:ArrayList<RoomList>) : RecyclerView.Adapter<RoomAdapterHolder>(){
    //var roomlist=mutableListOf<RoomList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapterHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.activity_room,parent,false)
        return RoomAdapterHolder(view)
    }
    override fun onBindViewHolder(holder: RoomAdapterHolder, position: Int) {
        holder.name.text=datalist[position].name
        holder.code.text=datalist[position].code
    }
    override fun getItemCount(): Int {
        return datalist.size
    }


}