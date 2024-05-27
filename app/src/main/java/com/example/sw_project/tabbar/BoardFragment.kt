package com.example.sw_project.tabbar

import android.content.Intent
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

        // '+' 플로팅 버튼 클릭시 게시판 추가 페이지로 이동
        _binding?.addBoard?.setOnClickListener {
            navigateToAddBoardActivity()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        // 게시물 리사이클러 뷰
        boardAdapter = BoardAdapter(roomCode)
        binding.boardsRecyclerView.adapter = boardAdapter
        binding.boardsRecyclerView.layoutManager = LinearLayoutManager(context)

        // 멤버 리사이클러 뷰
        memberAdapter = MemberAdapter(roomCode)
        binding.memberRecyclerView.adapter = memberAdapter
        binding.memberRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 스크롤 시 플로팅 버튼 가시성 조정
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

    // 멤버 데이터 설정하는 함수
    private fun loadMemberData() {
        val members = listOf(
            // *** 이곳에 데이터베이스에서 불러온 멤버 데이터 입력해주시면 됩니다. ***
            // 현재는 임시 데이터로 작성했습니다.
            MemberItem(profileImageUrl = "", uID = "mem1"),
            MemberItem(profileImageUrl = "", uID = "mem2"),
            MemberItem(profileImageUrl = "", uID = "mem3"),
            MemberItem(profileImageUrl = "", uID = "mem4"),
            MemberItem(profileImageUrl = "", uID = "mem5"),
        )
        memberAdapter.setMembers(members)
    }
    private fun loadMockData() {
        database.child("posts").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                coroutineScope.launch {
                    val postList = mutableListOf<BoardItem>()
                    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

                    dataSnapshot.children.forEach { postSnapshot ->
                        val post = postSnapshot.getValue(Post::class.java)
                        if (post != null) {
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

                    // 날짜 기준으로 정렬
                    postList.sortByDescending { dateFormatter.parse(it.dateText) ?: Date() }
                    boardAdapter.submitList(postList)
                    binding.boardsRecyclerView.scrollToPosition(0)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BoardFragment", "Error loading data from Firebase: ${databaseError.message}")
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