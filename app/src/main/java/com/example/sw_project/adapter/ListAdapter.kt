package com.example.sw_project.models.com.example.sw_project.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.sw_project.PersonalProfileActivity
import com.example.sw_project.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 데이터 클래스 정의
data class BoardItem(
    val roomID: String,
    val boardID: String,
    val profileImageUrl : String,
    val memberName: String,
    val imageUrl: String,
    var likeCount: Int,
    val contentText: String,
    val dateText: String
)

// DiffCallback 클래스 구현
class BoardDiffCallback : DiffUtil.ItemCallback<BoardItem>() {
    override fun areItemsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
        return oldItem.boardID == newItem.boardID
    }

    override fun areContentsTheSame(oldItem: BoardItem, newItem: BoardItem): Boolean {
        return oldItem == newItem
    }
}

// BoardAdapter 구현
class BoardAdapter(private val roomID: String?) : ListAdapter<BoardItem, BoardAdapter.BoardViewHolder>(
    BoardDiffCallback()
) {
    @SuppressLint("ClickableViewAccessibility")
    class BoardViewHolder(itemView: View, private val roomID: String?, private val adapter: BoardAdapter) : RecyclerView.ViewHolder(itemView) {
        private val profileContainer: LinearLayout = itemView.findViewById(R.id.profile_container)
        private val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        private val memberNameText: TextView = itemView.findViewById(R.id.member_name)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val likeCountText: TextView = itemView.findViewById(R.id.like_count_text)
        private val contentText: TextView = itemView.findViewById(R.id.content_text)
        private val dateText: TextView = itemView.findViewById(R.id.date_text)
        private val gestureDetector = GestureDetector(itemView.context, GestureListener(), null, false)

        init {
            contentText.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
                true
            }
        }

        fun bind(item: BoardItem) {
            memberNameText.text = item.memberName
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
                intent.putExtra("memberName", item.memberName)
                intent.putExtra("roomID", roomID)
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

        // 게시물 내용 더블 클릭 시 공감 카운트 증가 코드
        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentItem = adapter.getItem(position)
                    currentItem.likeCount += 1  // likeCount를 1 증가
                    Log.d("BoardAdapter", "Double tapped, like count updated: ${currentItem.likeCount}")
                    adapter.notifyItemChanged(position)  // 해당 포지션의 아이템을 업데이트
                    // *** DB 업데이트 ***
                    // 이곳에 해당 position 게시물의 증가된 currentItem.likeCount로 업데이트 하시면 됩니다.
                    // 각 게시물은 boardID로 구분하시면 됩니다.

                }
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
        return BoardViewHolder(view, roomID, this)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
