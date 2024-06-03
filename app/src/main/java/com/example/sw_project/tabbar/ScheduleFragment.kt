package com.example.sw_project.tabbar

import android.R.attr.fragment
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_NO_LOCALIZED_COLLATORS
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.sw_project.MainActivity
import com.example.sw_project.R
import com.example.sw_project.databinding.FragmentScheduleBinding
import java.io.FileInputStream
import java.io.FileOutputStream
import com.google.firebase.database.*


class ScheduleFragment : Fragment() {
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    private var userEmail: String? = null
    private var roomCode: String? = null

    lateinit var fname: String
    lateinit var str: String
    lateinit var calendarView: CalendarView
    lateinit var updateBtn: Button
    lateinit var deleteBtn: Button
    lateinit var saveBtn: Button
    lateinit var diaryTextView: TextView
    lateinit var diaryContent: TextView
    lateinit var title: TextView
    lateinit var contextEditText: EditText

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            roomCode = it.getString("roomCode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)

        Log.d("ScheduleFragment", "user email: ${userEmail}, room ID: $roomCode")

        calendarView = binding.calendarView
        diaryTextView = binding.diaryTextView
        saveBtn = binding.saveBtn
        deleteBtn = binding.deleteBtn
        updateBtn = binding.updateBtn
        diaryContent = binding.diaryContent
        title = binding.title
        contextEditText = binding.contextEditText

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            Log.d("ScheduleFragment", "날짜 클릭")
            diaryTextView.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            contextEditText.visibility = View.VISIBLE
            diaryContent.visibility = View.INVISIBLE
            updateBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
            diaryTextView.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            Log.d("ScheduleFragment", "$year $month $dayOfMonth")
            contextEditText.setText("")
            checkDay(year, month, dayOfMonth)
        }

        saveBtn.setOnClickListener {
            Log.d("ScheduleFragment", "저장버튼 클릭")
            saveDiary(fname)
            contextEditText.visibility = View.INVISIBLE
            saveBtn.visibility = View.INVISIBLE
            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            str = contextEditText.text.toString()
            Log.d("ScheduleFragment", "contents: $str")
            diaryContent.text = str
            diaryContent.visibility = View.VISIBLE
        }

        return binding.root
    }

    fun checkDay(cYear: Int, cMonth: Int, cDay: Int) {
        fname = "" + cYear + "-" + (cMonth + 1) + "-" + cDay

        val database = FirebaseDatabase.getInstance().reference
            .child("rooms").child(roomCode.orEmpty())
            .child("schedules").child(fname)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    str = snapshot.getValue(String::class.java).orEmpty()
                    contextEditText.visibility = View.INVISIBLE
                    diaryContent.visibility = View.VISIBLE
                    diaryContent.text = str
                    saveBtn.visibility = View.INVISIBLE
                    updateBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE

                    updateBtn.setOnClickListener {
                        contextEditText.visibility = View.VISIBLE
                        diaryContent.visibility = View.INVISIBLE
                        contextEditText.setText(str)
                        saveBtn.visibility = View.VISIBLE
                        updateBtn.visibility = View.INVISIBLE
                        deleteBtn.visibility = View.INVISIBLE
                        diaryContent.text = contextEditText.text
                    }

                    deleteBtn.setOnClickListener {
                        diaryContent.visibility = View.INVISIBLE
                        updateBtn.visibility = View.INVISIBLE
                        deleteBtn.visibility = View.INVISIBLE
                        contextEditText.setText("")
                        contextEditText.visibility = View.VISIBLE
                        saveBtn.visibility = View.VISIBLE
                        removeDiary(fname)
                    }
                } else {
                    contextEditText.visibility = View.VISIBLE
                    diaryContent.visibility = View.INVISIBLE
                    saveBtn.visibility = View.VISIBLE
                    updateBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ScheduleFragment", "Database error: ${error.message}")
            }
        })
    }

    fun removeDiary(readDay: String) {
        val database = FirebaseDatabase.getInstance().reference
            .child("rooms").child(roomCode.orEmpty())
            .child("schedules").child(readDay)
        database.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ScheduleFragment", "Diary removed successfully")
            } else {
                Log.e("ScheduleFragment", "Failed to remove diary")
            }
        }
    }

    fun saveDiary(readDay: String) {
        val database = FirebaseDatabase.getInstance().reference
            .child("rooms").child(roomCode.orEmpty())
            .child("schedules").child(readDay)
        val content = contextEditText.text.toString()
        database.setValue(content).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ScheduleFragment", "Diary saved successfully")
            } else {
                Log.e("ScheduleFragment", "Failed to save diary")
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance(userEmail: String?, roomCode: String?) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putString("roomCode", roomCode)
                }
            }
    }
}