package com.example.sw_project.models.com.example.sw_project.adapter

import android.content.Context
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
import com.example.sw_project.R


class PhotoAdapter(private val items:ArrayList<Uri>, val context: Context):
    RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    override fun getItemCount(): Int =items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        Glide.with(context).load(item)
            .override(500,500)
            .into(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView=LayoutInflater.from(parent.context).inflate(R.layout.item_photo,parent,false)
        return ViewHolder(inflatedView)
    }
    class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        private var view:View=v
        var image=v.findViewById<ImageView>(R.id.imageView)

        fun bind(listener:View.OnClickListener,item:String){
            view.setOnClickListener(listener)
        }
    }
}