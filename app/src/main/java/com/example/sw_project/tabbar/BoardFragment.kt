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
import androidx.viewbinding.ViewBindings
import com.example.sw_project.AddBoardActivity
import com.example.sw_project.BoardAdapter
import com.example.sw_project.BoardItem
import com.example.sw_project.R
import com.example.sw_project.databinding.FragmentBoardBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoardFragment : Fragment() {
    private var userEmail: String? = null
    private var roomID: Int = 0
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            roomID = it.getInt("roomID", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)
        // user email과 room id 로깅
        Log.d("BoardFragment", "user email: ${userEmail}, room ID: $roomID")

        // 리사이클러 뷰 설정
        setupRecyclerView()
        // 데이터 로드
        loadMockData()
        // '+' 플로팅 버튼 클릭시 게시판 추가 페이지로 이동
        _binding?.addBoard?.setOnClickListener {
            navigateToAddBoardActivity()
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        boardAdapter = BoardAdapter(roomID)
        binding.boardsRecyclerView.adapter = boardAdapter
        binding.boardsRecyclerView.layoutManager = LinearLayoutManager(context)

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

    private fun loadMockData() {
        val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())

        val mockData = listOf(
            // 이곳에 데이터베이스에서 불러온 게시물 데이터 입력해주시면 됩니다.
            // 현재는 임시 데이터로 작성했습니다.
            BoardItem(
                profileImageUrl = "",
                profileName = "Member 1",
                imageUrl = "https://images.unsplash.com/photo-1714905532906-0b9ec1b22dfa?q=80&w=2572&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                likeCount = 10,
                contentText = "Lorem ipsum dolor sit amet. Qui exercitationem architecto et dicta dolores ut galisum illum et laboriosam amet qui delectus voluptatem qui illum enim.",
                dateText = "2024년 05월 04일 13:15:20"
            ),
            BoardItem(
                profileImageUrl = "",
                profileName = "Member 2",
                imageUrl = "https://images.unsplash.com/photo-1715077856124-4405d115911d?q=80&w=2565&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                likeCount = 15,
                contentText = "Lorem ipsum dolor sit amet. Qui exercitationem architecto et dicta dolores ut galisum illum et laboriosam amet qui delectus voluptatem qui illum enim.",
                dateText = "2024년 05월 04일 13:15:25"
            ),
            BoardItem(
                profileImageUrl = "",
                profileName = "Member 3",
                imageUrl = "https://images.unsplash.com/photo-1715034136259-fed8a5b1f5fa?q=80&w=2670&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaGootby1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                likeCount = 5,
                contentText = "In detracto adipisci tacimates duo, nec oratio dolorem ex. Vix docendi consequuntur ei, in iriure eruditi tibique usu.",
                dateText = "2024년 05월 05일 14:20:20"
            )
        ).sortedByDescending { dateFormatter.parse(it.dateText) ?: Date() }

        boardAdapter.submitList(mockData)
    }


    private fun navigateToAddBoardActivity() {
        val intent = Intent(activity, AddBoardActivity::class.java).apply {
            putExtra("userEmail", userEmail)
            putExtra("roomID", roomID)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(userEmail: String?, roomID: Int) =
            BoardFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putInt("roomID", roomID)
                }
            }
    }
}