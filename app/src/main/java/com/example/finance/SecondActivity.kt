package com.example.finance

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    private var balance = 0

    private lateinit var db: AppDatabase
    private lateinit var emergencyExpensesDao: EmergencyExpensesDao
    private lateinit var emergencyBalanceDao: EmergencyBalanceDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            db = AppDatabase.getDatabase(this)
            EmergencyExpensesDao = db.emergencyExpenseDao()
            EmergencyBalanceDao = db.emergencyBalanceDao()

            val balanceText = findViewById<TextView>(R.id.ttBalance)
            val plusButton = findViewById<Button>(R.id.btPlus)
            val minusButton = findViewById<Button>(R.id.btMinus)
            val rightButton = findViewById<Button>(R.id.btRight)
            val leftButton = findViewById<Button>(R.id.btLeft)
            val goalText = findViewById<EditText>(R.id.ttGoal)
            val progressBar = findViewById<ProgressBar>(R.id.progressBar)


            leftButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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

                fun updateBalanceDisplay() {
                    balanceText.text = "$balance Kč"
                    val goal = goalText.text.toString().toFloatOrNull()
                    if (goal != null && goal > 0) {
                        val progress = ((balance.toFloat() / goal) * 100).toInt()
                        progressBar.progress = progress.coerceIn(0, 100)
                    }
                }

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

                        val currentBalance = Balance(
                            total = balance
                        )

                        lifecycleScope.launch {
                            expenseDao.insert(expense)
                            balanceDao.insertOrUpdate(currentBalance)
                        }

                        alertDialog.dismiss()
                    }
                }
            }

            plusButton.setOnClickListener { showDialog(true) }
            minusButton.setOnClickListener { showDialog(false) }



            insets
        }
    }
}