package com.example.sw_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val signInButton: Button = findViewById(R.id.signin_button)
        val signUpButton: Button = findViewById(R.id.signup_button)
        signInButton.setOnClickListener {
            signIn()
        }
        signUpButton.setOnClickListener {
            signUp()
        }

    }
    private fun signIn() {
        val idEditText: TextInputEditText = findViewById(R.id.id_edittext)
        val passwordEditText: TextInputEditText = findViewById(R.id.pwd_edittext)
        val id = idEditText.text.toString().trim()
        val pwd = passwordEditText.text.toString().trim()

        if(id.isNotEmpty() && pwd.isNotEmpty()) {
            auth.signInWithEmailAndPassword(id, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    moveStartPage(task.result.user)
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun signUp() {
        val idEditText: TextInputEditText = findViewById(R.id.id_edittext)
        val passwordEditText: TextInputEditText = findViewById(R.id.pwd_edittext)
        val id = idEditText.text.toString().trim()
        val pwd = passwordEditText.text.toString().trim()

        if(id.isNotEmpty() && pwd.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(id, pwd).addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {
                    moveStartPage(task.result.user)
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