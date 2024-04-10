package com.example.sw_project

import android.app.DatePickerDialog
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

class SignupGoogleActivity : AppCompatActivity() {
    private var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup_google)

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
        val nameEditText: TextInputEditText = findViewById(R.id.new_account_name)
        val birthEditText: TextInputEditText = findViewById(R.id.new_account_birth)
        val id = user?.email
        val name = nameEditText.text.toString().trim()
        val birthDate = birthEditText.text.toString().trim()

        if(name.isEmpty()) {
            Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(birthDate.isEmpty()) {
            Toast.makeText(this, "생년월일을 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 이곳에 이메일(구글 로그인으로 등록된 이메일), 이름, 생년월일을 데이터베이스에 저장하는 코드 구현하시면 됩니다.
        // 저장에 성공하면 StartActivity로 넘어가는 코드도 작성해주시면 됩니다.
        // 이메일, 이름 생년월일은 각각 id, name, birthdDate 변수에 담아져 있습니다.

        // 디버깅을 위해 Logcat에 이메일, 이름, 생년월일 출력하는 코드(다 작성하시고 지워주세요)
        Log.d("SignUp", "Email: $id, Name: $name, Birthdate: $birthDate")

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

