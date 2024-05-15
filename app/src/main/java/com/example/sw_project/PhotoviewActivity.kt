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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sw_project.models.com.example.sw_project.adapter.PhotoAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PhotoviewActivity : AppCompatActivity() {
    var list=ArrayList<Uri>()
    val adapter=PhotoAdapter(list,this)
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var imageView: ImageView
    private lateinit var imageActionsContainer: LinearLayout
    private lateinit var addImageContainer: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val albumid =intent.getIntExtra("albumID",0)
        Log.d("PhotoviewActivity",  "album ID: $albumid")
        //enableEdgeToEdge()
        setContentView(R.layout.activity_photoview)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        //권한 확인
        setupPermissions()
        setupImagePickerLauncher()

        var getimage=findViewById<FloatingActionButton>(R.id.add_photo)
        var recyclerview=findViewById<RecyclerView>(R.id.recyclerview)

        getimage.setOnClickListener {
            /*var intent=Intent(Intent.ACTION_PICK)
            intent.data=MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.action=Intent.ACTION_GET_CONTENT

            startActivity(intent/*,200*/)*/
            imagePickerLauncher.launch("image/*")
        }
        val layoutManager=LinearLayoutManager(this)
        recyclerview.layoutManager=layoutManager
        recyclerview.adapter=adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode== RESULT_OK/*&&requestCode==200*/){
            list.clear()

            if(data?.clipData!=null){
                val count=data.clipData!!.itemCount
                if(count>20){
                    Toast.makeText(applicationContext,"사진은 20장까지 선택 가능합닌다.",Toast.LENGTH_LONG)
                    return
                }
                for(i in 0 until count){
                    val imageUri=data.clipData!!.getItemAt(i).uri
                    list.add(imageUri)
                }
            }
            else{
                data?.data?.let { uri ->
                    val imageUri: Uri?=data?.data
                    if(imageUri!=null){
                        list.add(imageUri)
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

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
    private fun setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageView.setImageURI(uri)
                imageActionsContainer.visibility = View.VISIBLE
                addImageContainer.visibility = View.GONE
            }
        }
    }

}