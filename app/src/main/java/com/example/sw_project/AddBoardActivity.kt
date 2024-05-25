package com.example.sw_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.database.*
import com.example.sw_project.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class AddBoardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var imageView: ImageView
    private lateinit var contentEditText: TextInputEditText
    private lateinit var imageActionsContainer: LinearLayout
    private lateinit var addImageContainer: LinearLayout
    private lateinit var addImageButton: FloatingActionButton
    private lateinit var changeImageButton: MaterialButton
    private lateinit var removeImageButton: MaterialButton
    private lateinit var postButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var roomCode: String? = null
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_board)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 이전 프래그먼트로부터 데이터 받기
        roomCode = intent.getStringExtra("roomCode")

        Log.d("AddBoardActivity", "room code : $roomCode")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        setupViews()
        setupPermissions()
        setupImagePickerLauncher()
        setupListeners()

    }

    private fun setupViews() {
        imageView = findViewById(R.id.selected_image)
        contentEditText = findViewById(R.id.edit_content)
        imageActionsContainer = findViewById(R.id.image_actions_container)
        addImageContainer = findViewById(R.id.add_image_container)
        addImageButton = findViewById(R.id.add_image_button)
        changeImageButton = findViewById(R.id.change_image_button)
        removeImageButton = findViewById(R.id.remove_image_button)
        postButton = findViewById(R.id.post_button)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 스토리지 읽기 권한 요청
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }

    private fun setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                Glide.with(this)
                    .load(uri)
                    .override(1024, 1024)
                    .centerCrop()
                    .into(imageView)
                imageActionsContainer.visibility = View.VISIBLE
                addImageContainer.visibility = View.GONE
                imageUri = uri.toString()
            }
        }
    }

    private fun setupListeners() {
        // 이미지 추가 버튼 클릭 시
        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // 이미지 변경 버튼 클릭 시
        changeImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // 이미지 제거 버튼 클릭 시
        removeImageButton.setOnClickListener {
            imageView.setImageDrawable(null)
            imageActionsContainer.visibility = View.GONE
            addImageContainer.visibility = View.VISIBLE
            imageUri = ""
        }

        // 등록하기 버튼 클릭 시
        postButton.setOnClickListener {
            val content = contentEditText.text.toString().trim()
            if(content.isNotEmpty()) {
                post(content)
            } else {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
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

    private fun post(content: String) {
        val postId = database.child("posts").push().key ?: ""
        progressBar.visibility = View.VISIBLE
        if(!imageUri.isNullOrEmpty()) {
            postStorage(postId, content)
        } else {
            postDatabase(postId, content, "")
        }
    }

    private fun postStorage(postID: String, content: String) {
        val imageRef = storage.child("images/posts/$postID/board.jpg")

        Glide.with(this@AddBoardActivity)
            .asBitmap()
            .load(Uri.parse(imageUri))
            .override(1024, 1024)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val baos = ByteArrayOutputStream()
                    resource.compress(Bitmap.CompressFormat.JPEG, 70, baos) // JPEG 형식으로 압축
                    val imageData = baos.toByteArray()

                    // Firebase에 업로드
                    imageRef.putBytes(imageData)
                        .addOnSuccessListener {
                            it.storage.downloadUrl.addOnSuccessListener { uri ->
                                postDatabase(postID, content, uri.toString())
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AddBoardActivity, "이미지 업로드에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun postDatabase(postID: String, content: String, uri: String) {
        // Firebase Realtime Database에 데이터 저장
        val currentTime = System.currentTimeMillis()
        val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())
        val postTime = dateFormatter.format(Date(currentTime))
        val post = Post(
            roomCode ?: "",
            postID,
            auth.currentUser!!.uid,
            0, // 초기 likeCount는 0으로 설정
            uri,
            content,
            postTime
        )

        database.child("posts").child(postID).setValue(post)
            .addOnSuccessListener {
                Log.d("AddBoardActivity", "Post saved successfully")
                Toast.makeText(this@AddBoardActivity, "게시물이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                finish()
            }
            .addOnFailureListener { exception ->
                Log.e("AddBoardActivity", "Error saving post", exception)
                Toast.makeText(this@AddBoardActivity, "게시물 등록에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
    }
}
