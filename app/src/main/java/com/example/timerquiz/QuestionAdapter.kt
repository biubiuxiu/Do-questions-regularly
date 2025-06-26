package com.example.timerquiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuestionAdapter(
    private val questions: MutableList<Question>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuestionNumber: TextView = itemView.findViewById(R.id.tvQuestionNumber)
        val tvQuestionText: TextView = itemView.findViewById(R.id.tvQuestionText)
        val btnDeleteQuestion: ImageButton = itemView.findViewById(R.id.btnDeleteQuestion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.tvQuestionNumber.text = "${position + 1}."
        holder.tvQuestionText.text = question.text
        
        holder.btnDeleteQuestion.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = questions.size

    fun addQuestion(question: Question) {
        questions.add(question)
        notifyItemInserted(questions.size - 1)
    }

    fun removeQuestion(position: Int) {
        if (position >= 0 && position < questions.size) {
            questions.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, questions.size)
        }
    }

    fun getQuestions(): List<Question> = questions.toList()
}
