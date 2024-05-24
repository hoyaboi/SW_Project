package com.example.sw_project.models.com.example.sw_project.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.R

data class Album_list(val name:String)

class Album_Adapter(val Albumlist:ArrayList<Album_list>):RecyclerView.Adapter<Album_Adapter.AlbumAdapterHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapterHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_album,parent,false)
        return AlbumAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumAdapterHolder, position: Int) {
        holder.name.text=Albumlist[position].name
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
    }
    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener=onItemClickListener
    }
    private lateinit var itemClickListener: OnItemClickListener

    class AlbumAdapterHolder(v:View):RecyclerView.ViewHolder(v){
        var nameView: TextView =v.findViewById(R.id.album_name)
        val name=nameView
    }


    override fun getItemCount(): Int {
        return Albumlist.size
    }
}