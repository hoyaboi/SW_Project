package com.example.sw_project.models.com.example.sw_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.R

//data class RoomList(var name: String, var code: String)

data class listItem(val name: String, val code: String)

class Adapter(val Roomlist:ArrayList<listItem>) : RecyclerView.Adapter<Adapter.RoomAdapterHolder>(){
    //var roomlist=mutableListOf<RoomList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapterHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_room,parent,false)
        return RoomAdapterHolder(view)
    }
    override fun onBindViewHolder(holder: RoomAdapterHolder, position: Int) {
        holder.name.text=Roomlist[position].name

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
    }
    interface OnItemClickListener{
        fun onClick(v:View,position: Int)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener=onItemClickListener
    }
    private lateinit var itemClickListener: OnItemClickListener
    class RoomAdapterHolder(v:View) :RecyclerView.ViewHolder(v){
        var nameView:TextView=v.findViewById(R.id.roomView_name)
        val name=nameView
    }
    override fun getItemCount(): Int {
        return Roomlist.size
    }

}