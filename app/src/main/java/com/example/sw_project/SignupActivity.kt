package com.example.sw_project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val datePickerButton: Button = findViewById(R.id.date_picker_btn)
        val signUpButton: Button = findViewById(R.id.signup_button)
        val cancelButton: Button = findViewById(R.id.cancel_signup_button)

        datePickerButton.setOnClickListener {
            datePick()
        }
        signUpButton.setOnClickListener {
            signUp()
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun signUp() {
        val idEditText: TextInputEditText = findViewById(R.id.signup_email_text)
        val passwordEditText: TextInputEditText = findViewById(R.id.signup_pwd_text)
        val nameEditText: TextInputEditText = findViewById(R.id.new_account_name)
        val birthEditText: TextInputEditText = findViewById(R.id.new_account_birth)
        val id = idEditText.text.toString().trim()
        val pwd = passwordEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val birthDate = birthEditText.text.toString().trim()

        if (id.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (pwd.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (birthDate.isEmpty()) {
            Toast.makeText(this, "생년월일을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // firebase email 계정 생성
        // 이 부분에서 입력한 이메일과 패스워드로 계정 생성이 됩니다.
        auth.createUserWithEmailAndPassword(id, pwd).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // 디버깅을 위해 Logcat에 입력받은 이메일, 비번, 이름, 생년월일 출력하는 코드(다 작성하시고 지워주세요)
                Log.d("SignUp", "Email: $id, Password: $pwd, Name: $name, Birthdate: $birthDate")

                // 생성된 계정의 이메일과 이름, 생년월일 데이터를 데이터베이스에 저장하는 코드 구현하시면 됩니다.
                // 이메일, 이름, 생년월일 각각 id, name, birthDate 변수에 담아져 있습니다.


                // 회원 정보 등록 후 StartActivity로 넘어가는 코드
                startActivity(Intent(this, StartActivity::class.java))
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun datePick() {
        val birthDateEditText: TextInputEditText = findViewById(R.id.new_account_birth)

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDayOfMonth"
            birthDateEditText.setText(selectedDate)
        }, year, month, day)
        datePickerDialog.show()
    }
}

