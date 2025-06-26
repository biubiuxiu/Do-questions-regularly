package com.example.timerquiz

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    var answer: String = ""
) : Parcelable
