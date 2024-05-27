package com.example.sw_project.models.com.example.sw_project.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sw_project.PersonalProfileActivity
import com.example.sw_project.R

// 데이터 클래스 정의
data class MemberItem(
    val profileImageUrl: String,
    val uID: String,
    val uName: String
)

// MemberAdapter 구현
class MemberAdapter(private val roomCode: String?) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    private var members: List<MemberItem> = listOf()

    class MemberViewHolder(view: View, private val roomCode: String?) : RecyclerView.ViewHolder(view) {
        private val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        private val memberNameText: TextView = view.findViewById(R.id.member_name)
        private val memberContainer: LinearLayout = view.findViewById(R.id.member_container)

        fun bind(member: MemberItem) {
            // 프로필 이미지와 이름 로딩
            memberNameText.text = member.uName
            if (member.profileImageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(member.profileImageUrl)
                    .circleCrop()
                    .into(profileImageView)
            } else {
                profileImageView.setImageResource(R.drawable.tmp_face) // 기본 이미지 사용
            }

            // 프로필 클릭 시 개인 프로필 페이지로 이동
            memberContainer.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, PersonalProfileActivity::class.java)
                intent.putExtra("uID", member.uID)
                intent.putExtra("roomCode", roomCode)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view, roomCode)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])

        val params = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        when (position) {
            0 -> {
                params.leftMargin = 15.dpToPx(holder.itemView.context)
                params.rightMargin = 5.dpToPx(holder.itemView.context)
            }
            itemCount - 1 -> {
                params.leftMargin = 10.dpToPx(holder.itemView.context)
                params.rightMargin = 10.dpToPx(holder.itemView.context)
            }
            else -> {
                params.leftMargin = 5.dpToPx(holder.itemView.context)
                params.rightMargin = 15.dpToPx(holder.itemView.context)
            }
        }
        params.topMargin = 1.dpToPx(holder.itemView.context)
        params.bottomMargin = 1.dpToPx(holder.itemView.context)
    }

    override fun getItemCount() = members.size

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    fun setMembers(members: List<MemberItem>) {
        this.members = members
        notifyDataSetChanged()
    }

}
