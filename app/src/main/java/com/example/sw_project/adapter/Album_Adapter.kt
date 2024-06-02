package com.example.sw_project.models.com.example.sw_project.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.R
import com.example.sw_project.PhotoviewActivity
import com.example.sw_project.models.Album
import com.google.firebase.storage.FirebaseStorage

data class Album_list(val id:String, val name:String, val coverImageUrl:String)


class Album_Adapter(private val albumList: List<Album>) :
    RecyclerView.Adapter<Album_Adapter.AlbumViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener

    inner class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumCover: ImageView = itemView.findViewById(R.id.albumCover)
        val albumName: TextView = itemView.findViewById(R.id.album_name)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_album,parent,false)
        return AlbumViewHolder(view)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener=onItemClickListener
    }
    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }
    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val currentItem = albumList[position]

        // 앨범 커버 이미지 설정
        Glide.with(holder.itemView)
            .load(currentItem.coverImage) // 앨범 커버 이미지 URL
            .placeholder(R.drawable.tmp_face) // 이미지를 불러오는 동안 표시할 이미지
            .into(holder.albumCover)

        // 앨범 이름 설정
        holder.albumName.text = currentItem.name

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
    }


    override fun getItemCount(): Int {
        return albumList.size
    }
}