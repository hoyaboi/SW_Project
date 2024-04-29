package com.example.sw_project.tabbar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sw_project.R
import com.example.sw_project.databinding.FragmentAlbumBinding

class AlbumFragment : Fragment() {
    private var userEmail: String? = null
    private var roomID: Int = 0

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        // 여기서 userEmail과 roomID를 사용하여 UI 업데이트
        // user email과 room id 로깅
        Log.d("AlbumFragment", "user email: ${userEmail}, room ID: $roomID")




        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(userEmail: String?, roomID: Int) =
            AlbumFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putInt("roomID", roomID)
                }
            }
    }
}