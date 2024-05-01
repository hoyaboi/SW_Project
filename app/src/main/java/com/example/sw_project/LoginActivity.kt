package com.example.sw_project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val signInButton: Button = findViewById(R.id.signin_button)
        val signUpButton: Button = findViewById(R.id.signup_page_button)
//        val signUpGoogleButton : Button = findViewById(R.id.signin_google)

        signInButton.setOnClickListener {
            signIn()
        }
        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
//        signUpGoogleButton.setOnClickListener {
//            googleLogin()
//        }
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("1054487677216-u6f28mqmd1n7f3k96rr4inl0stv0qahu.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            return
        } else {
            Toast.makeText(this, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

//    // google 로그인
//    // 이 부분에서 google 계정 생성이 됩니다.
//    // 1. 데이터베이스에 로그인한 이메일과 매핑되는 (이메일, 이름, 생년월일)이 아직 등록 안 되어 있으면 SignupGoogleActivity로 이동
//    // 2. 등록된 사용자면 StartActivity로 이동하게 코드 수정해주시면 됩니다.
//    private fun googleLogin() {
//        val signInIntent = googleSignInClient.signInIntent
//        googleLoginLauncher.launch(signInIntent)
//    }
//    private val googleLoginLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if(result.resultCode == Activity.RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            try { // Google 로그인 성공, Firebase로 인증 정보 교환
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) { // Google 로그인 실패 처리
//                Log.w("GoogleLogin", "Google sign in failed", e)
//            }
//        }
//    }
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
//            if(task.isSuccessful) {
//                startActivity(Intent(this, SignupGoogleActivity::class.java))
//            } else {
//                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    // 이메일 로그인
    private fun signIn() {
        val idEditText: TextInputEditText = findViewById(R.id.id_edittext)
        val passwordEditText: TextInputEditText = findViewById(R.id.pwd_edittext)
        val id = idEditText.text.toString().trim()
        val pwd = passwordEditText.text.toString().trim()

        if(id.isNotEmpty() && pwd.isNotEmpty()) {
            auth.signInWithEmailAndPassword(id, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    // 실제 코드
//                    moveStartPage(task.result.user)

                    // 테스트 코드
                    val roomID = 1000
                    var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("roomID", roomID)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun moveStartPage(user:FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this, StartActivity::class.java))
        }
    }

}