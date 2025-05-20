package com.example.finance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmergencyExpenseAdapter(private val expenses: List<EmergencyExpense>) :
    RecyclerView.Adapter<EmergencyExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textValue: TextView = itemView.findViewById(R.id.textValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emergency_history, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.textDate.text = "${expense.day}.${expense.month}. ${expense.year}"
        holder.textValue.text = if (expense.value >= 0) "+${expense.value} Kč" else "${expense.value} Kč"
    }

    override fun getItemCount(): Int = expenses.size
}
