package com.example.sw_project.tabbar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.sw_project.LoginActivity
import com.example.sw_project.R
import com.example.sw_project.StartActivity
import com.example.sw_project.databinding.FragmentProfileBinding
import com.example.sw_project.setting.BoardSettingActivity
import com.example.sw_project.setting.PersonalSettingActivity
import com.example.sw_project.setting.ProfileImageSettingActivity
import com.example.sw_project.setting.ShowRoomCodeActivity
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {
    private var userEmail: String? = null
    private var roomCode: String? = null
    private var name: String? = null
    private var birthDate: String? = null
    private var profileUri: String? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var userProfileListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            roomCode = it.getString("roomCode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // 여기서 UI 업데이트

        loadProfileData()       // 프로필 정보 표시
        profileImageSetting()   // 프로필 이미지 설정
        personalSetting()       // 개인 설정
        boardSetting()          // 게시물 수정
        newBoardNotifSetting()  // 새 게시물 알림 설정
        scheduleNotifSetting()  // 일정 랄림 설정
        showRoomCode()          // 방 입장 코드 확인
        signout()               // 로그아웃
        exitRoom()              // 방 나가기

        return binding.root
    }

    private fun loadProfileData() { // 데이터베이스에서 사용자 정보를 불러와 프로필 표시
        userProfileListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 데이터 스냅샷에서 이름과 생일 데이터를 읽음
                name = snapshot.child("name").getValue(String::class.java)
                birthDate = snapshot.child("birthDate").getValue(String::class.java)

                // UI 업데이트
                if (name != null && birthDate != null) {
                    // 날짜 형식 변환
                    val inputFormat = SimpleDateFormat("yyyy/M/d", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                    val date = inputFormat.parse(birthDate.toString())
                    val formattedDate = outputFormat.format(date ?: Date())

                   binding.profileNameText.text = "이름 : $name"
                   binding.birthdayText.text = "생일 : $formattedDate"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ProfileFragment", "loadProfileData:onCancelled", error.toException())
            }
        }
        database.child("users").child(auth.uid!!).addValueEventListener(userProfileListener!!)

        // 프로필 이미지 URI 불러오기
        database.child("rooms").child(roomCode!!).child("participants").child(auth.uid!!).child("profileUri")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    profileUri = snapshot.getValue(String::class.java) ?: ""
                    Glide.with(requireContext())
                        .load(profileUri!!.ifEmpty { R.drawable.tmp_face })  // 빈 URI인 경우 기본 이미지 사용
                        .circleCrop()
                        .into(binding.profileImage)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w("ProfileFragment", "Failed to load profile image: $error")
                }
            })
    }

    private fun profileImageSetting() {
        binding.profileImageSettingConatainer.setOnClickListener {
            val intent = Intent(requireContext(), ProfileImageSettingActivity::class.java)
            intent.putExtra("roomCode", roomCode)
            intent.putExtra("profileUri", profileUri)
            startActivity(intent)
        }
    }

    private fun personalSetting() {
        binding.personalSettingConatainer.setOnClickListener {
            val intent = Intent(requireContext(), PersonalSettingActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("birthDate", birthDate)
            startActivity(intent)
        }
    }

    private fun boardSetting() {
        binding.boardSettingConatainer.setOnClickListener {
            val intent = Intent(requireContext(), BoardSettingActivity::class.java)
            intent.putExtra("roomCode", roomCode)
            startActivity(intent)
        }
    }

    private fun newBoardNotifSetting() {
        // 새 게시물 알림 처리 코드
    }

    private fun scheduleNotifSetting() {
        // 일정 알림 처리 코드
    }

    private fun showRoomCode() {
        binding.roomCodeContainer.setOnClickListener {
            val intent = Intent(requireContext(), ShowRoomCodeActivity::class.java)
            intent.putExtra("roomCode", roomCode)
            startActivity(intent)
        }
    }

    private fun signout() {
        binding.signoutText.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun exitRoom() {
        binding.exitRoomText.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("방 나가기")
                .setMessage("방을 나가시겠습니까?\n작성한 게시물은 삭제되지 않습니다.")
                .setPositiveButton("나가기") { dialog, which ->
                    database.child("rooms").child(roomCode!!).child("participants").child(auth.uid!!).removeValue()
                        .addOnSuccessListener {
                            startActivity(Intent(requireContext(), StartActivity::class.java))
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "다시 시도하세요.", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun onDestroyView() {
        userProfileListener?.let { listener ->
            database.child("users").child(auth.uid!!).removeEventListener(listener)
        }
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(userEmail: String?, roomCode: String?) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putString("roomCode", roomCode)
                }
            }
    }
}