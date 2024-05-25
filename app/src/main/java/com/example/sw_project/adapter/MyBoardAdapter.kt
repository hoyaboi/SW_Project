package com.example.sw_project.models.com.example.sw_project.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sw_project.R
import com.example.sw_project.StartActivity
import com.example.sw_project.setting.ModifyBoardActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import java.util.Date

data class Post(
    val imageUri: String,
    val content: String,
    val postID: String,
    val date: Date? = null
)

class MyBoardAdapter(private val posts: List<Post>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_IMAGE = 0
    private val TYPE_TEXT = 1

    override fun getItemViewType(position: Int): Int {
        return if (posts[position].imageUri.isNotEmpty()) TYPE_IMAGE else TYPE_TEXT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val itemSize = screenWidth / 3 // 공백을 고려한 아이템 크기

        return if (viewType == TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board_img, parent, false)
            view.layoutParams = RecyclerView.LayoutParams(itemSize, itemSize)
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board_text, parent, false)
            view.layoutParams = RecyclerView.LayoutParams(itemSize, itemSize)
            TextViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = posts[position]
        if (holder.itemViewType == TYPE_IMAGE) {
            (holder as ImageViewHolder).bind(post)
        } else {
            (holder as TextViewHolder).bind(post)
        }
    }

    override fun getItemCount() = posts.size

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ShapeableImageView = view.findViewById(R.id.imageView)

        fun bind(post: Post) {
            Glide.with(itemView.context)
                .load(post.imageUri)
                .into(imageView)

            imageView.setOnClickListener {
                moveToModifyActivity(post)
            }
        }
    }

    inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: MaterialTextView = view.findViewById(R.id.textView)

        fun bind(post: Post) {
            textView.text = post.content

            textView.setOnClickListener {
                moveToModifyActivity(post)
            }
        }
    }

    private fun moveToModifyActivity(post: Post) {
        val intent = Intent(context, ModifyBoardActivity::class.java)
        intent.putExtra("imageUri", post.imageUri)
        intent.putExtra("content", post.content)
        intent.putExtra("postID", post.postID)
        context.startActivity(intent)
    }
}