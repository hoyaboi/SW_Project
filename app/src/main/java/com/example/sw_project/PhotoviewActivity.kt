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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.*


class PhotoviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoviewBinding
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var getResult: ActivityResultLauncher<Intent>
    private lateinit var storageReference: StorageReference
    private val imagelist = arrayListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val albumId = intent.getStringExtra("albumID")
        if (albumId == null) {
            Toast.makeText(this, "앨범 ID를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        else {
            loadPhotosFromDatabase(albumId)
        }
        binding.addPhoto.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        setupPermissions()
        setupFirebaseStorage()
        setupRecyclerView()
        setupActivityResult()
    }

    private fun checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            openGallery()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        getResult.launch(intent)
    }
    private fun setupPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        }
    }

    private fun setupFirebaseStorage() {
        storageReference = FirebaseStorage.getInstance().reference
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(imagelist, this)
        binding.recyclerview.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerview.adapter = photoAdapter
    }

    private fun setupActivityResult() {
        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val albumId = intent.getStringExtra("albumID")
                if (albumId != null) {
                    if (result.data?.clipData != null) {
                        val count = result.data?.clipData!!.itemCount
                        for (index in 0 until count) {
                            val imageUri = result.data?.clipData!!.getItemAt(index).uri
                            uploadImageToFirebaseStorage(albumId, imageUri)
                        }
                    } else {
                        val imageUri = result.data?.data
                        if (imageUri != null) {
                            uploadImageToFirebaseStorage(albumId, imageUri)
                        }
                    }
                }
            }
        }
    }
    private fun uploadImageToFirebaseStorage(albumId: String?, imageUri: Uri) {
        if (albumId != null) {
            val photoRef = storageReference.child("albums/$albumId/${imageUri.lastPathSegment}")
            val uploadTask = photoRef.putFile(imageUri)
            uploadTask.addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("PhotoviewActivity", "Image uploaded successfully: $uri")
                    imagelist.add(uri)
                    photoAdapter.notifyDataSetChanged()
                }.addOnFailureListener { exception ->
                    Log.e("PhotoviewActivity", "Error getting download URL", exception)
                }
            }.addOnFailureListener { exception ->
                Log.e("PhotoviewActivity", "Upload failed", exception)
            }
        }
    }


    private fun loadPhotosFromDatabase(albumId: String?) {
        if (albumId != null) {
            val storageReference = FirebaseStorage.getInstance().getReference("albums/$albumId")
            storageReference.listAll()
                .addOnSuccessListener { result ->
                    if (result.items.isNotEmpty()) {
                        val coverImageRef = result.items[0]
                        coverImageRef.downloadUrl.addOnSuccessListener { uri ->
                            val albumReference = FirebaseDatabase.getInstance().getReference("albums").child(albumId)
                            albumReference.child("coverImage").setValue(uri.toString())
                        }
                    }
                    result.items.forEach { photoReference ->
                        photoReference.downloadUrl.addOnSuccessListener { uri ->
                            imagelist.add(uri)
                            photoAdapter.notifyDataSetChanged()
                        }.addOnFailureListener { exception ->
                            Log.e("PhotoviewActivity", "Error getting download URL", exception)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("PhotoviewActivity", "Error listing files", exception)
                }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "권한이 거부되었습니다. 이미지를 업로드하려면 저장소 권한을 허용해야 합니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}