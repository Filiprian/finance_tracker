// Add this at the top
package com.example.finance

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    private var balance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        expenseDao = db.expenseDao()

        val balanceText = findViewById<TextView>(R.id.ttBalance)
        val plusButton = findViewById<Button>(R.id.btPlus)
        val minusButton = findViewById<Button>(R.id.btMinus)

        fun updateBalanceDisplay() {
            balanceText.text = "$balance Kč"
        }

        fun showDialog(isAddition: Boolean) {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.income, null)
            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)

            val valueInput = dialogView.findViewById<EditText>(R.id.etValue)
            val dayInput = dialogView.findViewById<EditText>(R.id.etDay)
            val monthInput = dialogView.findViewById<EditText>(R.id.etMonth)
            val yearInput = dialogView.findViewById<EditText>(R.id.etYear)
            val actionButton = dialogView.findViewById<Button>(R.id.btAdd)

            actionButton.text = if (isAddition) "+" else "-"

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            actionButton.setOnClickListener {
                val value = valueInput.text.toString().toIntOrNull()
                val day = dayInput.text.toString().toInt()
                val month = monthInput.text.toString().toInt()
                val year = yearInput.text.toString().toInt()

                if (value != null && day in 1..31 && month in 1..12) {
                    val finalValue = if (isAddition) value else -value
                    balance += finalValue
                    updateBalanceDisplay()

                    val expense = Expense(
                        value = finalValue,
                        day = day,
                        month = month,
                        year = year
                    )

                    lifecycleScope.launch {
                        expenseDao.insert(expense)
                    }

                    alertDialog.dismiss()
                }
            }
        }

        plusButton.setOnClickListener { showDialog(true) }
        minusButton.setOnClickListener { showDialog(false) }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
