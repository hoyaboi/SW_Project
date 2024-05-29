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

/*class Album_Adapter(private val context: Context, private val albumList: List<Album>) :
    RecyclerView.Adapter<Album_Adapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albumList[position]
        holder.albumName.text = album.name

        // Fetch the first image in the album to set as the cover
        val storageReference = FirebaseStorage.getInstance().getReference("albums/${album.id}")
        storageReference.listAll()
            .addOnSuccessListener { result ->
                if (result.items.isNotEmpty()) {
                    result.items[0].downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(context).load(uri).into(holder.albumCover)
                    }
                }
            }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PhotoviewActivity::class.java)
            intent.putExtra("albumID", album.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val albumName: TextView = itemView.findViewById(R.id.album_name)
        val albumCover: ImageView = itemView.findViewById(R.id.image)
    }
}*/

class Album_Adapter(val Albumlist:ArrayList<Album>):RecyclerView.Adapter<Album_Adapter.AlbumAdapterHolder>() {

    private lateinit var itemClickListener: OnItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumAdapterHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_album,parent,false)
        return AlbumAdapterHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumAdapterHolder, position: Int) {
        val album = Albumlist[position]
        holder.nameView.text = album.name
        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it,position)
        }
    }
    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener=onItemClickListener
    }
    interface OnItemClickListener{
        fun onClick(v: View, position: Int)
    }


    class AlbumAdapterHolder(v:View):RecyclerView.ViewHolder(v){
        var nameView: TextView =v.findViewById(R.id.album_name)
        val name=nameView
    }


    override fun getItemCount(): Int {
        return Albumlist.size
    }
}