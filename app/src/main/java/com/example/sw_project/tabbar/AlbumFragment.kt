package com.example.sw_project.tabbar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sw_project.AddAlbumActivity
import com.example.sw_project.AddBoardActivity
import com.example.sw_project.Album_Adapter
import com.example.sw_project.Album_list
import com.example.sw_project.PhotoviewActivity
import com.example.sw_project.databinding.FragmentAlbumBinding


class AlbumFragment : Fragment() {
    private var userEmail: String? = null
    private var roomID: String? = null

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private lateinit var binding2:FragmentAlbumBinding
    val Albumlist= arrayListOf<Album_list>()
    val Albumadapter=Album_Adapter(Albumlist)

    lateinit var Albumname:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            roomID = it.getString("roomID")
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


        //setContentView(view)

        binding.recyclerview.layoutManager=LinearLayoutManager(this.activity,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerview.adapter=Albumadapter
//앨범 클릭시 앨범 index를 넘겨주며 사진 목록 화면으로 넘어감
        Albumadapter.setItemClickListener(object:Album_Adapter.OnItemClickListener{
          override fun onClick(v:View,position:Int){
            val intent=Intent(activity, PhotoviewActivity::class.java)
          intent.putExtra("albumID",position)
        startActivity(intent)
        }
        })
        //테스트
        Albumlist.add(Album_list("강원도여행"))
        Albumlist.add(Album_list("여름휴가"))
        //val name: String=intent.getStringExtra("albumname").toString()
        //val check=intent.getIntExtra("check",0)
        //if(check==1){
        //    Albumlist.add(Album_list(name))
        //}
        _binding?.addAlbum?.setOnClickListener {
            val intent = Intent(activity, AddAlbumActivity::class.java)
            startActivity(intent)
        }




        Log.d("AlbumFragment", "user email: ${userEmail}, room ID: $roomID")
        return binding.root
        //return View(requireActivity())
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