package com.example.sw_project.setting

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sw_project.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class PersonalSettingActivity : AppCompatActivity() {
    private var name: String? = null
    private var birthDate: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var newNameEditText: TextInputEditText
    private lateinit var newBirthEditText: TextInputEditText
    private lateinit var datePickerButton: MaterialButton
    private lateinit var changeButton: MaterialButton
    private lateinit var cancelButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_setting)

        // 상태표시줄 색상 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightgrey)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        name = intent.getStringExtra("name")
        birthDate = intent.getStringExtra("birthDate")

        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        newNameEditText = findViewById(R.id.new_name_textfield)
        newBirthEditText = findViewById(R.id.new_birth_textfield)
        datePickerButton = findViewById(R.id.date_picker_btn)
        changeButton = findViewById(R.id.change_button)
        cancelButton = findViewById(R.id.cancel_button)

        newNameEditText.setText(name)
        newBirthEditText.setText(birthDate)
    }

    private fun setupListeners() {
        datePickerButton.setOnClickListener {
            datePick()
        }

        changeButton.setOnClickListener {
            saveChagnedUserInfo()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun datePick() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDayOfMonth"
            newBirthEditText.setText(selectedDate)
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun saveChagnedUserInfo() {
        val newName = newNameEditText.text.toString()
        val newBirthDate = newBirthEditText.text.toString()
        val email: String = auth.currentUser!!.email.toString()

        if(newName.isEmpty() || newBirthDate.isEmpty()) {
            Toast.makeText(applicationContext, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val userMap = hashMapOf(
            "name" to newName,
            "email" to email,
            "birthDate" to newBirthDate
        )

        database.child("users").child(auth.uid!!).setValue(userMap)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "변경되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "변경에 실패했습니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show()
            }
    }
}
