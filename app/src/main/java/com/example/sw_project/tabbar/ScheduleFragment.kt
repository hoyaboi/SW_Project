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


class ScheduleFragment : Fragment() {
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        mainActivity = context as MainActivity
    }
    private var userEmail: String? = null
    private var roomID: String? = null

    lateinit var fname: String
    lateinit var str: String
    lateinit var calendarView: CalendarView
    lateinit var updateBtn: Button
    lateinit var deleteBtn:Button
    lateinit var saveBtn: Button
    lateinit var diaryTextView: TextView
    lateinit var diaryContent:TextView
    lateinit var title: TextView
    lateinit var contextEditText: EditText

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            roomID = it.getString("roomID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        // 여기서 userEmail과 roomID를 사용하여 UI 업데이트
        // user email과 room id 로깅
        Log.d("ScheduleFragment", "user email: ${userEmail}, room ID: $roomID")

        val view2=inflater.inflate(R.layout.fragment_schedule,container,false)

        calendarView=view2.findViewById(R.id.calendarView)
        diaryTextView=view2.findViewById(R.id.diaryTextView)
        saveBtn=view2.findViewById(R.id.saveBtn)
        deleteBtn=view2.findViewById(R.id.deleteBtn)
        updateBtn=view2.findViewById(R.id.updateBtn)
        diaryContent=view2.findViewById(R.id.diaryContent)
        title=view2.findViewById(R.id.title)
        contextEditText=view2.findViewById(R.id.contextEditText)

        //title.text="캘린더"

        _binding?.calendarView?.setOnDateChangeListener{view, year,month,dayOfMonth->
            Log.d("ScheduleFragment", "날짜 클릭")
            binding.diaryTextView.visibility=View.VISIBLE
            binding.saveBtn.visibility=View.VISIBLE
            binding.contextEditText.visibility=View.VISIBLE
            binding.diaryContent.visibility=View.INVISIBLE
            binding.updateBtn.visibility=View.INVISIBLE
            binding.deleteBtn.visibility=View.INVISIBLE
            binding.diaryTextView.text=String.format("%d / %d / %d",year, month+1,dayOfMonth)
            Log.d("ScheduleFragment", "$year     $month    $dayOfMonth")
            binding.contextEditText.setText("")
            //(context as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commit()
            //(context as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commit()
            checkDay(year,month,dayOfMonth)
        }

        _binding?.saveBtn?.setOnClickListener{
            Log.d("ScheduleFragment", "저장버튼 클릭")
            saveDiary(fname)
            binding.contextEditText.visibility=View.INVISIBLE
            binding.saveBtn.visibility = View.INVISIBLE
            binding.updateBtn.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.VISIBLE
            str = binding.contextEditText.text.toString()
            Log.d("ScheduleFragment", "contents: ${str}")
            binding.diaryContent.text = str
            binding.diaryContent.visibility = View.VISIBLE
        }

        return binding.root
    }

    fun checkDay(cYear:Int,cMonth:Int,cDay:Int){
        //저장할 파일 이름 설정
        fname=""+cYear+"-"+(cMonth+1)+""+"-"+cDay+".txt"

        var fileInputStream: FileInputStream
        try{

            val filePath = mainActivity.getExternalFilesDir(null).toString()
            //fileInputStream=mainActivity.openFileInput("${filePath}+/+${fname}")
            fileInputStream=mainActivity.openFileInput(fname)

            val fileData=ByteArray(fileInputStream.available())
            fileInputStream.read(fileData)
            fileInputStream.close()
            str=String(fileData)
            binding.contextEditText.visibility=View.INVISIBLE
            binding.diaryContent.visibility=View.VISIBLE
            binding.diaryContent.text=str
            binding.saveBtn.visibility=View.INVISIBLE
            binding.updateBtn.visibility=View.VISIBLE
            binding.deleteBtn.visibility=View.VISIBLE
            _binding?.updateBtn?.setOnClickListener{
                binding.contextEditText.visibility=View.VISIBLE
                binding.diaryContent.visibility=View.INVISIBLE
                binding.contextEditText.setText(str)
                binding.saveBtn.visibility=View.VISIBLE
                binding.updateBtn.visibility = View.INVISIBLE
                binding.deleteBtn.visibility = View.INVISIBLE
                binding.diaryContent.text = binding.contextEditText.text
            }
            _binding?.deleteBtn?.setOnClickListener {
                binding.diaryContent.visibility = View.INVISIBLE
                binding.updateBtn.visibility = View.INVISIBLE
                binding.deleteBtn.visibility = View.INVISIBLE
                binding.contextEditText.setText("")
                binding.contextEditText.visibility = View.VISIBLE
                binding.saveBtn.visibility = View.VISIBLE
                removeDiary(fname)
            }
            if (diaryContent.text == null) {
                binding.diaryContent.visibility = View.INVISIBLE
                binding.updateBtn.visibility = View.INVISIBLE
                binding.deleteBtn.visibility = View.INVISIBLE
                binding.diaryTextView.visibility = View.VISIBLE
                binding.saveBtn.visibility = View.VISIBLE
                binding.contextEditText.visibility = View.VISIBLE
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @SuppressLint("WrongConstant")
    fun removeDiary(readDay: String?){
        var fileOutputStream: FileOutputStream
        try{
            fileOutputStream=mainActivity.openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS)
            val content=""
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        }
        catch(e:java.lang.Exception){
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    fun saveDiary(readDay: String?) {
        var fileOutputStream: FileOutputStream
        try {
            fileOutputStream = mainActivity.openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS)
            val content = binding.contextEditText.text.toString()
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userEmail: String?, roomID: String?) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString("userEmail", userEmail)
                    putString("roomID", roomID)
                }
            }
    }
}