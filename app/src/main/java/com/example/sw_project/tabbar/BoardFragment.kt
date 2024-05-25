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

class BoardFragment : Fragment() {
    private var userEmail: String? = null
    private var roomCode: String? = null
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
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
        val databaseReference = FirebaseDatabase.getInstance().reference
        val postsRef = databaseReference.child("posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postList = mutableListOf<BoardItem>()
                val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

                for (postSnapshot in dataSnapshot.children) {
                    try {
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let {
                            // Post 객체를 BoardItem 객체로 변환
                            val boardItem = BoardItem(
                                roomCode = it.roomCode,
                                boardID = it.postId,
                                profileImageUri = "",//it.profileImageUri,
                                uID = it.uid, // 이 uid를 가진 유저의 이름 나오게 하기
                                imageUri = it.imageUri,
                                likeCount = it.likeCount,
                                contentText = it.content,
                                dateText = it.postTime
                            )
                            postList.add(boardItem)
                        }
                    } catch (e: Exception) {
                        Log.e("BoardFragment", "Error parsing post: ${e.message}")
                    }
                }

                try {
                    postList.sortByDescending { dateFormatter.parse(it.dateText) ?: Date() }
                    // Ensure this runs on the UI thread
                    activity?.runOnUiThread {
                        boardAdapter.submitList(postList)
                        val recyclerView: RecyclerView = binding.boardsRecyclerView
                        recyclerView.scrollToPosition(0)
                    }
                } catch (e: Exception) {
                    Log.e("BoardFragment", "Error sorting or submitting list: ${e.message}")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BoardFragment", "Error loading data from Firebase: ${databaseError.message}")
            }
        })
    }


    /*private fun loadMockData() {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val postsRef = databaseReference.child("posts")

        postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val postList = mutableListOf<BoardItem>()
                val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(BoardItem::class.java)
                    post?.let {
                        postList.add(it)
                    }
                }

                postList.sortByDescending { dateFormatter.parse(it.dateText) ?: Date() }
                boardAdapter.submitList(postList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BoardFragment", "Error loading data from Firebase: ${databaseError.message}")
            }
        })
    }*/

    // 게시물 데이터 설정하는 함수

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