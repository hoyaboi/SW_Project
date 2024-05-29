package com.example.sw_project.models.com.example.sw_project.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.sw_project.FullScreenPhotoActivity
import com.example.sw_project.R


class PhotoAdapter():RecyclerView.Adapter<PhotoAdapter.ViewHolder>(){
    lateinit var imagelist:ArrayList<Uri>
    lateinit var context: Context

    constructor(imagelist:ArrayList<Uri>,context: Context):this(){
        this.imagelist=imagelist
        this.context=context
    }
    override fun getItemCount(): Int {
        return imagelist.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Glide.with(context)
        //    .load(imagelist[position])
        //    .into(holder.imageView)
        val imageUri = imagelist[position]
        Glide.with(context)
            .load(imageUri)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            val intent = Intent(context, FullScreenPhotoActivity::class.java)
            intent.putExtra("photoUri", imageUri)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater:LayoutInflater=LayoutInflater.from(parent.context)
        val view:View=inflater.inflate(R.layout.item_photo,parent,false)
        return ViewHolder(view)
    }
    class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        val imageView: ImageView=v.findViewById(R.id.imageView)
    }
}