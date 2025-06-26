package com.example.timerquiz

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    companion object {
        const val ACTION_START_TIMER = "START_TIMER"
        const val ACTION_STOP_TIMER = "STOP_TIMER"
        const val EXTRA_DURATION = "DURATION"
        const val EXTRA_QUESTIONS = "QUESTIONS"
        
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "timer_channel"
    }

    private var countDownTimer: CountDownTimer? = null
    private var questions: List<Question> = emptyList()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TIMER -> {
                val duration = intent.getLongExtra(EXTRA_DURATION, 0L)
                questions = intent.getParcelableArrayListExtra<Question>(EXTRA_QUESTIONS) ?: emptyList()
                startTimer(duration)
            }
            ACTION_STOP_TIMER -> {
                stopTimer()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startTimer(duration: Long) {
        val notification = createNotification("定时器启动", formatTime(duration))
        startForeground(NOTIFICATION_ID, notification)

        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = formatTime(millisUntilFinished)
                val notification = createNotification("定时器运行中", "距离答题时间还有 $timeLeft")
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)
            }

            override fun onFinish() {
                showQuizActivity()
                stopSelf()
            }
        }.start()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        stopForeground(true)
        stopSelf()
    }

    private fun showQuizActivity() {
        val intent = Intent(this, QuizActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or 
                   Intent.FLAG_ACTIVITY_CLEAR_TOP or
                   Intent.FLAG_ACTIVITY_SINGLE_TOP
            putParcelableArrayListExtra(QuizActivity.EXTRA_QUESTIONS, ArrayList(questions))
        }
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "定时器通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示定时器状态"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
}
