package com.example.sw_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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

class AddBoardActivity : AppCompatActivity() {
    private var uid: String? = null
    private var roomID: String? = null

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

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_board)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setupViews()
        setupPermissions()
        setupImagePickerLauncher()

        // 이전 프래그먼트로부터 데이터 받기
        uid = intent.getStringExtra("userEmail")
        roomID = intent.getStringExtra("roomID")

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
        }
        // 등록하기 버튼 클릭 시
        postButton.setOnClickListener {
            if(contentEditText.text.toString().trim().isNotEmpty()) {
                post()
                finish()
            } else {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
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
                imageView.setImageURI(uri)
                imageActionsContainer.visibility = View.VISIBLE
                addImageContainer.visibility = View.GONE
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

    private fun post() {
        progressBar.visibility = View.VISIBLE

        coroutineScope.launch {
            // 게시글 내용과 작성 시간 가져오기
            val content = contentEditText.text.toString().trim()
            val currentTime = System.currentTimeMillis()
            val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())
            val postTime = dateFormatter.format(Date(currentTime))

            // 이미지 URI 가져오기
            val imageUri: Uri? = (imageView.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                saveImageToTempFile(bitmap)
            }

            // Logging for debugging
            Log.d("AddBoardActivity", "User Email: $uid")
            Log.d("AddBoardActivity", "Room ID: $roomID")
            Log.d("AddBoardActivity", "Content: $content")
            Log.d("AddBoardActivity", "Post Time: $postTime")
            imageUri?.let {
                Log.d("AddBoardActivity", "Image URI: $it")
            } ?: Log.d("AddBoardActivity", "No Image selected")

            // Firebase Realtime Database에 데이터 저장
            val databaseReference = FirebaseDatabase.getInstance().reference
            val postId = databaseReference.child("posts").push().key ?: ""
            val post = Post(
                postId,
                uid ?: "",
                roomID ?: "",
                content,
                postTime,
                imageUri.toString(),
                0 // 초기 likeCount는 0으로 설정
            )

            databaseReference.child("posts").child(postId).setValue(post)
                .addOnSuccessListener {
                    Log.d("AddBoardActivity", "Post saved successfully")
                    Toast.makeText(this@AddBoardActivity, "게시물이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.e("AddBoardActivity", "Error saving post", exception)
                    Toast.makeText(this@AddBoardActivity, "게시물 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            progressBar.visibility = View.GONE
        }
    }
    /*private fun post() {
        progressBar.visibility = View.VISIBLE

        coroutineScope.launch {
            // 게시글 내용과 작성 시간 가져오기
            val content = contentEditText.text.toString().trim()
            val currentTime = System.currentTimeMillis()
            val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss", Locale.getDefault())
            val postTime = dateFormatter.format(Date(currentTime))

            // 이미지 URI 가져오기
            val imageUri: Uri? = (imageView.drawable as? BitmapDrawable)?.bitmap?.let { bitmap ->
                saveImageToTempFile(bitmap)
            }

            // Logging for debugging
            Log.d("AddBoardActivity", "User Email: $userEmail")
            Log.d("AddBoardActivity", "Room ID: $roomID")
            Log.d("AddBoardActivity", "Content: $content")
            Log.d("AddBoardActivity", "Post Time: $postTime")
            imageUri?.let {
                Log.d("AddBoardActivity", "Image URI: $it")
            } ?: Log.d("AddBoardActivity", "No Image selected")

            // firebase 데이터베이스에 userEmail, roomID, 이미지, 게시글, 작성시간 저장하는 코드 작성하시면 됩니다.
            // 1. firebase storage에 이미지 업로드
            // 2. 이미지가 성공적으로 업로드 되면, 이미지 URL과 게시글 데이터 firebase realtime database에 저장



            progressBar.visibility = View.GONE
        }
    }*/

    private fun saveImageToTempFile(bitmap: Bitmap): Uri {
        // 이미지 파일을 임시 파일로 저장하고 Uri 반환
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
        return Uri.fromFile(file)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        clearTempFiles()  // 앱 종료 시 임시 파일 삭제
    }

    private fun clearTempFiles() {
        // 임시 파일을 정리하는 함수
        val tempFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        tempFilesDir?.listFiles()?.forEach { file ->
            if (file.name.startsWith("share_image_")) {
                file.delete()
            }
        }
    }
}
