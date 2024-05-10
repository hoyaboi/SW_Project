package com.example.sw_project

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 데이터 클래스 정의
data class BoardItem(
    val profileImageUrl : String,
    val profileName: String,
    val imageUrl: String,
    val likeCount: Int,
    val contentText: String,
    val dateText: String
)

// DiffCallback 클래스 구현
class BoardDiffCallback : DiffUtil.ItemCallback<BoardItem>() {
    override fun areItemsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
        return oldItem.imageUrl == newItem.imageUrl
    }

    override fun areContentsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
        return oldItem == newItem
    }
}

// BoardAdapter 구현
class BoardAdapter(private val roomID: Int) : ListAdapter<BoardItem, BoardAdapter.BoardViewHolder>(BoardDiffCallback()) {
    class BoardViewHolder(itemView: View, private val roomID: Int) : RecyclerView.ViewHolder(itemView) {
        private val profileContainer: LinearLayout = itemView.findViewById(R.id.profile_container)
        private val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        private val profileNameText: TextView = itemView.findViewById(R.id.profile_name)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val likeCountText: TextView = itemView.findViewById(R.id.like_count_text)
        private val contentText: TextView = itemView.findViewById(R.id.content_text)
        private val dateText: TextView = itemView.findViewById(R.id.date_text)

        fun bind(item: BoardItem) {
            profileNameText.text = item.profileName
            likeCountText.text = "공감 ${item.likeCount}개"
            contentText.text = item.contentText
            val inputFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            val date = inputFormat.parse(item.dateText) ?: Date()
            dateText.text = outputFormat.format(date)

            // 프로필 클릭 시 개인 프로필 페이지로 이동
            profileContainer.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, PersonalProfileActivity::class.java)
                intent.putExtra("profileName", item.profileName)
                intent.putExtra("roomID, ", roomID)
                context.startActivity(intent)
            }

            // 프로필 이미지 로딩
            if (item.profileImageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(item.profileImageUrl)
                    .circleCrop()
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.tmp_face) // 프로필 이미지가 없는 경우 기본 이미지 사용
            }

            // 게시물 이미지 로딩
            if (item.imageUrl.isNotEmpty()) {
                imageView.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .into(imageView)
            } else {
                imageView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_item, parent, false)
        return BoardViewHolder(view, roomID)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
