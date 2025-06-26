package com.example.timerquiz

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etHours: TextInputEditText
    private lateinit var etMinutes: TextInputEditText
    private lateinit var etSeconds: TextInputEditText
    private lateinit var etQuestion: TextInputEditText
    private lateinit var btnAddQuestion: MaterialButton
    private lateinit var btnStartTimer: MaterialButton
    private lateinit var btnStopTimer: MaterialButton
    private lateinit var rvQuestions: RecyclerView
    private lateinit var tvStatus: TextView

    // 新增的UI组件
    private lateinit var toggleTimerType: MaterialButtonToggleGroup
    private lateinit var btnCountdown: MaterialButton
    private lateinit var btnSpecificTime: MaterialButton
    private lateinit var layoutCountdown: LinearLayout
    private lateinit var layoutSpecificTime: LinearLayout
    private lateinit var etDate: TextInputEditText
    private lateinit var etTime: TextInputEditText

    private lateinit var questionAdapter: QuestionAdapter
    private val questions = mutableListOf<Question>()

    // 时间相关变量
    private var selectedDateTime: Calendar? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        checkOverlayPermission()
    }

    private fun initViews() {
        etHours = findViewById(R.id.etHours)
        etMinutes = findViewById(R.id.etMinutes)
        etSeconds = findViewById(R.id.etSeconds)
        etQuestion = findViewById(R.id.etQuestion)
        btnAddQuestion = findViewById(R.id.btnAddQuestion)
        btnStartTimer = findViewById(R.id.btnStartTimer)
        btnStopTimer = findViewById(R.id.btnStopTimer)
        rvQuestions = findViewById(R.id.rvQuestions)
        tvStatus = findViewById(R.id.tvStatus)

        // 新增的UI组件
        toggleTimerType = findViewById(R.id.toggleTimerType)
        btnCountdown = findViewById(R.id.btnCountdown)
        btnSpecificTime = findViewById(R.id.btnSpecificTime)
        layoutCountdown = findViewById(R.id.layoutCountdown)
        layoutSpecificTime = findViewById(R.id.layoutSpecificTime)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)

        // 默认选择倒计时模式
        toggleTimerType.check(R.id.btnCountdown)
    }

    private fun setupRecyclerView() {
        questionAdapter = QuestionAdapter(questions) { position ->
            questionAdapter.removeQuestion(position)
            updateUI()
        }
        rvQuestions.layoutManager = LinearLayoutManager(this)
        rvQuestions.adapter = questionAdapter
    }

    private fun setupClickListeners() {
        btnAddQuestion.setOnClickListener {
            addQuestion()
        }

        btnStartTimer.setOnClickListener {
            startTimer()
        }

        btnStopTimer.setOnClickListener {
            stopTimer()
        }

        // 定时器类型切换
        toggleTimerType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnCountdown -> {
                        layoutCountdown.visibility = View.VISIBLE
                        layoutSpecificTime.visibility = View.GONE
                    }
                    R.id.btnSpecificTime -> {
                        layoutCountdown.visibility = View.GONE
                        layoutSpecificTime.visibility = View.VISIBLE
                    }
                }
            }
        }

        // 日期选择
        etDate.setOnClickListener {
            showDatePicker()
        }

        // 时间选择
        etTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

            if (selectedDateTime == null) {
                selectedDateTime = Calendar.getInstance()
            }
            selectedDateTime?.set(Calendar.YEAR, selectedYear)
            selectedDateTime?.set(Calendar.MONTH, selectedMonth)
            selectedDateTime?.set(Calendar.DAY_OF_MONTH, selectedDay)

            etDate.setText(dateFormat.format(selectedCalendar.time))
        }, year, month, day).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            if (selectedDateTime == null) {
                selectedDateTime = Calendar.getInstance()
            }
            selectedDateTime?.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedDateTime?.set(Calendar.MINUTE, selectedMinute)
            selectedDateTime?.set(Calendar.SECOND, 0)

            val timeCalendar = Calendar.getInstance()
            timeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            timeCalendar.set(Calendar.MINUTE, selectedMinute)

            etTime.setText(timeFormat.format(timeCalendar.time))
        }, hour, minute, true).show()
    }

    private fun addQuestion() {
        val questionText = etQuestion.text.toString().trim()
        if (questionText.isEmpty()) {
            Toast.makeText(this, "请输入题目内容", Toast.LENGTH_SHORT).show()
            return
        }

        val question = Question(text = questionText)
        questionAdapter.addQuestion(question)
        etQuestion.text?.clear()
        updateUI()
    }

    private fun startTimer() {
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
            return
        }

        if (questions.isEmpty()) {
            Toast.makeText(this, "请至少添加一道题目", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = when (toggleTimerType.checkedButtonId) {
            R.id.btnCountdown -> {
                // 倒计时模式
                val hours = etHours.text.toString().toIntOrNull() ?: 0
                val minutes = etMinutes.text.toString().toIntOrNull() ?: 0
                val seconds = etSeconds.text.toString().toIntOrNull() ?: 0
                val totalSeconds = hours * 3600 + minutes * 60 + seconds

                if (totalSeconds <= 0) {
                    Toast.makeText(this, "请设置有效的时间", Toast.LENGTH_SHORT).show()
                    return
                }
                totalSeconds * 1000L
            }
            R.id.btnSpecificTime -> {
                // 指定时间模式
                if (selectedDateTime == null) {
                    Toast.makeText(this, "请选择日期和时间", Toast.LENGTH_SHORT).show()
                    return
                }

                val currentTime = System.currentTimeMillis()
                val targetTime = selectedDateTime!!.timeInMillis

                if (targetTime <= currentTime) {
                    Toast.makeText(this, "请选择未来的时间", Toast.LENGTH_SHORT).show()
                    return
                }

                targetTime - currentTime
            }
            else -> {
                Toast.makeText(this, "请选择定时器类型", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val intent = Intent(this, TimerService::class.java).apply {
            action = TimerService.ACTION_START_TIMER
            putExtra(TimerService.EXTRA_DURATION, duration)
            putParcelableArrayListExtra(TimerService.EXTRA_QUESTIONS, ArrayList(questions))
        }
        startForegroundService(intent)

        updateTimerStatus(true)
        Toast.makeText(this, "定时器已启动", Toast.LENGTH_SHORT).show()
    }

    private fun stopTimer() {
        val intent = Intent(this, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP_TIMER
        }
        startService(intent)

        updateTimerStatus(false)
        Toast.makeText(this, "定时器已停止", Toast.LENGTH_SHORT).show()
    }

    private fun updateTimerStatus(isRunning: Boolean) {
        if (isRunning) {
            tvStatus.text = "定时器运行中..."
            tvStatus.visibility = View.VISIBLE
            btnStartTimer.isEnabled = false
            btnStopTimer.isEnabled = true
        } else {
            tvStatus.visibility = View.GONE
            btnStartTimer.isEnabled = true
            btnStopTimer.isEnabled = false
        }
    }

    private fun updateUI() {
        btnStartTimer.isEnabled = questions.isNotEmpty()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
        }
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        Toast.makeText(this, getString(R.string.grant_permission), Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}
