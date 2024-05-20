package com.example.sw_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.databinding.ActivityPhotoviewBinding
import com.example.sw_project.models.com.example.sw_project.adapter.PhotoAdapter
import com.example.sw_project.models.com.example.sw_project.adapter.listItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PhotoviewActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhotoviewBinding
    lateinit var photoAdapter:PhotoAdapter
    private lateinit var getResult:ActivityResultLauncher<Intent>
    //var imagelist: <Uri>
    var imagelist= arrayListOf<Uri>()
    //val adapter=PhotoAdapter(list,this)
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val albumid =intent.getIntExtra("albumID",0)
        Log.d("PhotoviewActivity",  "album ID: $albumid")
        //enableEdgeToEdge()
        binding=ActivityPhotoviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        //권한 확인
        setupPermissions()

        photoAdapter= PhotoAdapter(imagelist,this)
        //binding.recyclerview.layoutManager=LinearLayoutManager(this)
        binding.recyclerview.layoutManager=GridLayoutManager(this,3)
        //binding.recyclerview.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.recyclerview.adapter=photoAdapter
        //var getimage=findViewById<FloatingActionButton>(R.id.add_photo)
        //var recyclerview=findViewById<RecyclerView>(R.id.recyclerview)

        binding.addPhoto.setOnClickListener {
            var intent=Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            getResult.launch(intent)
        }

        getResult=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== RESULT_OK){
                if(it.data!!.clipData!=null){
                    val count=it.data!!.clipData!!.itemCount
                    for(index in 0 until count){
                        val imageUri=it.data!!.clipData!!.getItemAt(index).uri

                        // imageUri이 사진 정보
                        imagelist.add(imageUri)
                    }
                }
                else{
                    val imageUri=it.data!!.data
                    imagelist.add(imageUri!!)
                }
                photoAdapter.notifyDataSetChanged()
            }
        }
    }

    /*ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode== RESULT_OK){
            if(it.data!!.clipData!=null) {
                val count = it.data!!.clipData!!.itemCount
                for(index in 0 until count){
                    val imageUri=it.data!!.clipData!!.getItemAt(index).uri
                    imagelist.add(imageUri)
                }
            }
            else{
                val imageUri=it.data!!.data
                imagelist.add(imageUri!!)
            }
            photoAdapter.notifyDataSetChanged()
        }
    }*/

    private fun setupPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 스토리지 읽기 권한 요청
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                Toast.makeText(this, "권한이 거부되었습니다. 이미지를 업로드하려면 저장소 권한을 허용해야 합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }


}