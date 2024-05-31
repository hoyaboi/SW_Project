package com.example.sw_project.setting

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sw_project.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext

class ModifyBoardActivity : AppCompatActivity() {
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
    private lateinit var modifyButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var content: String? = null
    private var imageUri: String? = null
    private var postID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_board)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        content = intent.getStringExtra("content") ?: return
        imageUri = intent.getStringExtra("imageUri") ?: return
        postID = intent.getStringExtra("postID") ?: return

        Log.d("ModifyBoardActivity", "image uri : $imageUri, post id : $postID")

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        setupViews()
        setupPermissions()
        setupImagePickerLauncher()
        loadBoardData()
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
        modifyButton = findViewById(R.id.modify_button)
        deleteButton = findViewById(R.id.delete_button)
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

    private fun loadBoardData() {
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUri)
                .into(imageView)
            imageActionsContainer.visibility = View.VISIBLE
            addImageContainer.visibility = View.GONE
        }
        contentEditText.setText(content)
    }

    private fun setupListeners() {
        addImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        changeImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        removeImageButton.setOnClickListener {
            imageView.setImageDrawable(null)
            imageActionsContainer.visibility = View.GONE
            addImageContainer.visibility = View.VISIBLE
            imageUri = ""
        }

        modifyButton.setOnClickListener {
            val modifiedContent = contentEditText.text.toString().trim()
            if(modifiedContent.isNotEmpty()) {
                modifyBoard(modifiedContent)
            } else {
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            deleteBoard()
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

    private fun modifyBoard(content: String) {
        progressBar.visibility = View.VISIBLE
        if(!imageUri.isNullOrEmpty()) {
            modifyStorage(content)
        } else {
            updateDatabase(content, "")
        }
    }

    private fun modifyStorage(content: String) {
        val imageRef = storage.child("images/posts/$postID/board.jpg")

        Glide.with(this@ModifyBoardActivity)
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
                                updateDatabase(content, uri.toString())
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@ModifyBoardActivity, "이미지 업로드에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun deleteBoard() {
        AlertDialog.Builder(this)
            .setTitle("게시물 삭제")
            .setMessage("이 게시물을 정말 삭제하시겠습니까?")
            .setPositiveButton("확인") { dialog, which ->
                database.child("posts").child(postID!!).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "게시물 삭제에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun updateDatabase(content: String, uri: String) {
        val updatedBoard = hashMapOf<String, Any>(
            "content" to content,
            "imageUri" to uri,
        )

        database.child("posts").child(postID!!).updateChildren(updatedBoard)
            .addOnSuccessListener {
                Toast.makeText(this, "게시물이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "게시물 수정에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
    }

}

