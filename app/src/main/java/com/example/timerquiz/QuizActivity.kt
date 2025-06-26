package com.example.timerquiz

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class QuizActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUESTIONS = "QUESTIONS"
    }

    private lateinit var tvProgress: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var etAnswer: TextInputEditText
    private lateinit var btnConfirm: MaterialButton

    private var questions: MutableList<Question> = mutableListOf()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置全屏和锁屏显示
        setupFullScreen()
        
        setContentView(R.layout.activity_quiz)

        initViews()
        loadQuestions()
        displayCurrentQuestion()
        setupClickListeners()
    }

    private fun setupFullScreen() {
        // 设置全屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        // 在锁屏上显示
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // 隐藏系统UI
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
    }

    private fun initViews() {
        tvProgress = findViewById(R.id.tvProgress)
        tvQuestion = findViewById(R.id.tvQuestion)
        etAnswer = findViewById(R.id.etAnswer)
        btnConfirm = findViewById(R.id.btnConfirm)
    }

    private fun loadQuestions() {
        questions = intent.getParcelableArrayListExtra<Question>(EXTRA_QUESTIONS)?.toMutableList() 
            ?: mutableListOf()
        
        if (questions.isEmpty()) {
            Toast.makeText(this, "没有题目可显示", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupClickListeners() {
        btnConfirm.setOnClickListener {
            handleConfirmClick()
        }

        // 防止用户通过返回键退出
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 不执行任何操作，阻止返回
            }
        })
    }

    private fun displayCurrentQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            
            // 更新进度显示
            tvProgress.text = getString(
                R.string.question_number, 
                currentQuestionIndex + 1
            ) + " / " + getString(R.string.total_questions, questions.size)
            
            // 显示题目
            tvQuestion.text = question.text
            
            // 清空答案输入框
            etAnswer.text?.clear()
            etAnswer.requestFocus()
            
            // 更新按钮文本
            btnConfirm.text = if (currentQuestionIndex == questions.size - 1) {
                getString(R.string.finish)
            } else {
                getString(R.string.next_question)
            }
        }
    }

    private fun handleConfirmClick() {
        val answer = etAnswer.text.toString().trim()
        
        if (answer.isEmpty()) {
            Toast.makeText(this, "请输入答案", Toast.LENGTH_SHORT).show()
            return
        }

        // 保存答案
        if (currentQuestionIndex < questions.size) {
            questions[currentQuestionIndex].answer = answer
        }

        // 检查是否还有下一题
        if (currentQuestionIndex < questions.size - 1) {
            // 显示下一题
            currentQuestionIndex++
            displayCurrentQuestion()
        } else {
            // 所有题目完成，显示完成信息并关闭
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        Toast.makeText(this, "所有题目已完成！", Toast.LENGTH_LONG).show()
        
        // 可以在这里保存答案到文件或数据库
        saveAnswers()
        
        // 延迟关闭，让用户看到完成提示
        btnConfirm.postDelayed({
            finish()
        }, 2000)
    }

    private fun saveAnswers() {
        // 这里可以实现保存答案的逻辑
        // 例如保存到SharedPreferences、文件或数据库
        val sharedPref = getSharedPreferences("quiz_answers", MODE_PRIVATE)
        val editor = sharedPref.edit()
        
        questions.forEachIndexed { index, question ->
            editor.putString("question_${index}_text", question.text)
            editor.putString("question_${index}_answer", question.answer)
        }
        
        editor.putLong("completion_time", System.currentTimeMillis())
        editor.apply()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // 重新隐藏系统UI
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }
}
