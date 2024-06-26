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
import com.google.firebase.database.*

// 데이터 클래스 정의
data class BoardItem(
    val roomCode: String,
    val boardID: String,
    val profileImageUri : String,
    val uID: String,
    val uName: String,
    val imageUri: String,
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
class BoardAdapter(private val roomCode: String?) : ListAdapter<BoardItem, BoardAdapter.BoardViewHolder>(
    BoardDiffCallback()
) {
    @SuppressLint("ClickableViewAccessibility")
    class BoardViewHolder(itemView: View, private val roomCode: String?, private val adapter: BoardAdapter) : RecyclerView.ViewHolder(itemView) {
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
            memberNameText.text = item.uName
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
                intent.putExtra("uID", item.uID)
                intent.putExtra("roomCode", roomCode)
                context.startActivity(intent)
            }

            // 프로필 이미지 로딩
            if (item.profileImageUri.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(item.profileImageUri)
                    .circleCrop()
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.tmp_face) // 프로필 이미지가 없는 경우 기본 이미지 사용
            }

            // 게시물 이미지 로딩
            if (item.imageUri.isNotEmpty()) {
                imageView.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(item.imageUri)
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

                    // Firebase Realtime Database에서 해당 게시물의 좋아요 카운트 가져오기
                    val postId = currentItem.boardID
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("posts").child(postId).child("likeCount")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    val currentLikeCount = dataSnapshot.getValue(Int::class.java)
                                    currentLikeCount?.let {
                                        // 현재 좋아요 카운트에 1을 더한 값을 Firebase Realtime Database에 업데이트
                                        database.child("posts").child(postId).child("likeCount")
                                            .setValue(it + 1)
                                            .addOnSuccessListener {
                                                //val updatedLikeCount = (it ?: "0") + 1
                                                //Log.d("BoardAdapter", "Like count updated in database: $updatedLikeCount")

                                            }
                                            .addOnFailureListener { exception ->
                                                Log.e("BoardAdapter", "Error updating like count in database", exception)
                                            }
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.e("BoardAdapter", "Error fetching like count from database: ${databaseError.message}")
                            }
                        })
                }
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
        return BoardViewHolder(view, roomCode, this)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
