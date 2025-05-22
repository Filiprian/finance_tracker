package com.example.finance

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmergencyExpenseAdapter(
    private val expenses: MutableList<EmergencyExpense>,
    private val expenseDao: EmergencyExpensesDao
) :
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

        holder.itemView.findViewById<Button>(R.id.buttonMore).setOnClickListener {
            val dialogView = LayoutInflater.from(holder.itemView.context).inflate(R.layout.history_detail, null)

            val textDate = dialogView.findViewById<TextView>(R.id.textDate)
            val textValue = dialogView.findViewById<TextView>(R.id.textValue)
            val textCategory = dialogView.findViewById<TextView>(R.id.textCategory)

            textDate.text = "${expense.day}.${expense.month}. ${expense.year}"
            textValue.text = if (expense.value >= 0) "+${expense.value} Kč" else "${expense.value} Kč"
            textCategory.text = "${expense.category}"
                .trimIndent()

            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
                .setView(dialogView)
                .setCancelable(true)

            val alertDialog = dialogBuilder.create()

            alertDialog.show()

            val deleteButton = dialogView.findViewById<Button>(R.id.buttonDelete)

            deleteButton.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Deleted", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    expenseDao.delete(expense)

                    withContext(Dispatchers.Main) {
                        expenses.removeAt(position)
                        notifyItemRemoved(position)
                        alertDialog.dismiss()
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int = expenses.size
}
