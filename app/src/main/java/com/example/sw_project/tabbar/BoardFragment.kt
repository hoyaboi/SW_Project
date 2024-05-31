package com.example.sw_project.tabbar

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.AddBoardActivity
import com.example.sw_project.models.com.example.sw_project.adapter.BoardAdapter
import com.example.sw_project.models.com.example.sw_project.adapter.BoardItem
import com.example.sw_project.models.com.example.sw_project.adapter.MemberAdapter
import com.example.sw_project.models.com.example.sw_project.adapter.MemberItem
import com.example.sw_project.databinding.FragmentBoardBinding
import com.example.sw_project.models.Member
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.database.*
import com.example.sw_project.models.Post
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BoardFragment : Fragment() {
    private var userEmail: String? = null
    private var roomCode: String? = null
    private var _binding: FragmentBoardBinding? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var memberAdapter: MemberAdapter

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
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        setupRecyclerView() // 리사이클러 뷰 설정
        loadMockData()      // 게시물 데이터 로드
        loadMemberData()    // 멤버 데이터 로드

        // 프로필 변경 감지
        setupProfileChangeListener()
        setupProfileImageChangeListener()

        // '+' 플로팅 버튼 클릭시 게시판 추가 페이지로 이동
        _binding?.addBoard?.setOnClickListener {
            navigateToAddBoardActivity()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        // 멤버 리사이클러 뷰
        memberAdapter = MemberAdapter(roomCode)
        binding.memberRecyclerView.adapter = memberAdapter
        binding.memberRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 게시물 리사이클러 뷰
        boardAdapter = BoardAdapter(roomCode)
        binding.boardsRecyclerView.adapter = boardAdapter
        binding.boardsRecyclerView.layoutManager = LinearLayoutManager(context)

        // 스크롤 시 플로팅 버튼 visibility 조정
        binding.boardsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { // 아래로 스크롤
                    binding.addBoard.hide()
                } else if (dy < 0) { // 위로 스크롤
                    binding.addBoard.show()
                }
            }
        })
    }

    // 데이터 로드
    private fun loadMockData() {
        database.child("posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coroutineScope.launch {
                    val postList = mutableListOf<BoardItem>()
                    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

                    dataSnapshot.children.forEach { postSnapshot ->
                        val post = postSnapshot.getValue(Post::class.java)
                        if (post != null && post.roomCode == roomCode) {
                            val profileUri = async(Dispatchers.IO) { getProfileUri(post.uid) }
                            val userName = async(Dispatchers.IO) { getUserName(post.uid) }

                            // Post 객체를 BoardItem 객체로 변환
                            val boardItem = BoardItem(
                                roomCode = post.roomCode,
                                boardID = post.postId,
                                profileImageUri = profileUri.await(),
                                uID = post.uid,
                                uName = userName.await(),
                                imageUri = post.imageUri,
                                likeCount = post.likeCount,
                                contentText = post.content,
                                dateText = post.postTime
                            )

                            postList.add(boardItem)
                        }
                    }

                    postList.sortByDescending { dateFormatter.parse(it.dateText) ?: Date() } // 날짜 기준 정렬
                    activity?.runOnUiThread {
                        boardAdapter.submitList(postList) {
                            binding.boardsRecyclerView.scrollToPosition(0)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BoardFragment", "Error loading data from Firebase: ${databaseError.message}")
            }
        })
    }

    private fun loadMemberData() {
        database.child("rooms").child(roomCode!!).child("participants").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                coroutineScope.launch {
                    val memberList = mutableListOf<MemberItem>()

                    snapshot.children.forEach { memberSnapshot ->
                        val member = memberSnapshot.getValue(Member::class.java)
                        if (member != null) {
                            val userName = async(Dispatchers.IO) { getUserName(member.uID) }

                            // member item 추가
                            val memberItem = MemberItem(
                                profileImageUrl = member.profileUri,
                                uID = member.uID,
                                uName = userName.await()
                            )

                            memberList.add(memberItem)
                        }
                    }

                    memberList.sortBy { it.uName }
                    activity?.runOnUiThread {
                        memberAdapter.setMembers(memberList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BoardFragment", "Error loading data from Firebase: ${error.message}")
            }
        })
    }

    suspend fun getProfileUri(uid: String): String {
        val snapshot = database.child("rooms").child(roomCode!!).child("participants").child(uid).child("profileUri").get().await()
        return snapshot.getValue(String::class.java) ?: ""
    }

    suspend fun getUserName(uid: String): String {
        val snapshot = database.child("users").child(uid).child("name").get().await()
        return snapshot.getValue(String::class.java) ?: "Unknown"
    }

    private fun navigateToAddBoardActivity() {
        val intent = Intent(activity, AddBoardActivity::class.java).apply {
            putExtra("userEmail", userEmail)
            putExtra("roomCode", roomCode)
        }
        startActivity(intent)
    }

    private fun setupProfileChangeListener() {
        // 유저 프로필 정보의 변경을 감지
        database.child("users").child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 프로필 정보가 변경되면 게시물 목록을 다시 로드
                loadMockData()
                loadMemberData()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BoardFragment", "Error loading user profile data: ${error.message}")
            }
        })
    }

    private fun setupProfileImageChangeListener() {
        // 유저 프로필 정보의 변경을 감지
        database.child("rooms").child(roomCode!!).child("participants").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 프로필 정보가 변경되면 게시물 목록을 다시 로드
                    loadMockData()
                    loadMemberData()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BoardFragment", "Error loading user profile data: ${error.message}")
                }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(userEmail: String?, roomCode: String?) =
            BoardFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putString("roomCode", roomCode)
                }
            }
    }
}