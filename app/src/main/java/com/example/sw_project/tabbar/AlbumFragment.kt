package com.example.sw_project.tabbar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sw_project.AddAlbumActivity
import com.example.sw_project.models.com.example.sw_project.adapter.Album_Adapter
import com.example.sw_project.models.com.example.sw_project.adapter.Album_list
import com.example.sw_project.PhotoviewActivity
import com.example.sw_project.databinding.FragmentAlbumBinding
import com.google.firebase.database.*
import com.example.sw_project.models.Album


class AlbumFragment : Fragment() {
    private var userEmail: String? = null
    private var roomCode: String? = null

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    val Albumlist= arrayListOf<Album>()
    val Albumadapter= Album_Adapter(Albumlist)

    private val databaseReference = FirebaseDatabase.getInstance().getReference("albums")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            roomCode = it.getString("roomID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        // 여기서 userEmail과 roomID를 사용하여 UI 업데이트
        // user email과 room id 로깅
        //binding2=FragmentAlbumBinding.inflate(layoutInflater)
        val view=binding.root

        setupRecyclerView()
        setupAddAlbumButton()
        loadAlbumsFromDatabase()
        //setContentView(view)
        Log.d("AlbumFragment", "user email: $userEmail, room code: $roomCode")
        return view
    }
    private fun setupRecyclerView() {
        binding.recyclerview.layoutManager = GridLayoutManager(activity, 2)
        binding.recyclerview.adapter = Albumadapter

        Albumadapter.setItemClickListener(object : Album_Adapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(activity, PhotoviewActivity::class.java)
                intent.putExtra("albumID", Albumlist[position].id)
                startActivity(intent)
            }
        })
    }
    private fun setupAddAlbumButton() {
        binding.addAlbum.setOnClickListener {
            val intent = Intent(activity, AddAlbumActivity::class.java)
            startActivity(intent)
        }
    }
    private fun loadAlbumsFromDatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Albumlist.clear()
                for (albumSnapshot in snapshot.children) {
                    val album = albumSnapshot.getValue(Album::class.java)
                    album?.let {
                        val updatedAlbum = Album(it.name, it.coverImage, albumSnapshot.key ?: "")
                        Albumlist.add(updatedAlbum)
                        Log.d("AlbumFragment", "Data loaded successfully: $snapshot")
                    }
                }
                Albumadapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("AlbumFragment", "Failed to read value.", error.toException())
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {
        @JvmStatic
        fun newInstance(userEmail: String?, roomID: String?) =
            AlbumFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putString("roomID", roomID)
                }
            }
    }
}