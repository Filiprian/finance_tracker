package com.example.finance

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
    private lateinit var emergencyGoalDao: EmergencyGoalDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        db = AppDatabase.getDatabase(this)
        emergencyExpensesDao = db.emergencyExpenseDao()
        emergencyBalanceDao = db.emergencyBalanceDao()
        emergencyGoalDao = db.emergencyGoalDao()


        val balanceText = findViewById<TextView>(R.id.ttBalance)
        val warningText = findViewById<TextView>(R.id.ttWarning)
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

        fun updateBalanceDisplay() {
            balanceText.text = "$balance Kč"
            val goal = goalText.text.toString().toFloatOrNull()
            if (goal != null && goal > 0) {
                val progress = ((balance.toFloat() / goal) * 100).toInt()
                progressBar.progress = progress.coerceIn(0, 100)
            }
        }

        goalText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val goal = goalText.text.toString().toIntOrNull()
                if (goal != null) {
                    warningText.visibility = View.INVISIBLE
                    val emergencyGoal = EmergencyGoal(
                        id = 1,
                        total = goal
                    )
                    lifecycleScope.launch {
                        emergencyGoalDao.insertOrUpdate(emergencyGoal)
                        Log.d("EMERGENCY_GOAL", "Saved goal: ${emergencyGoal.total}")
                    }
                    lifecycleScope.launch {
                        val savedGoals = emergencyGoalDao.getBalance()
                        Log.d("EMERGENCY_GOAL", "Loaded goals: $savedGoals")
                    }

                } else {
                    warningText.visibility = View.VISIBLE
                }

                updateBalanceDisplay() // call the same method to update the bar
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lifecycleScope.launch {
            val savedBalance = db.emergencyBalanceDao().getBalance()
            balance = savedBalance?.total ?: 0
            updateBalanceDisplay()
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

                    val emergencyExpense = EmergencyExpense(
                        value = finalValue,
                        day = day,
                        month = month,
                        year = year
                    )

                    val emergencyBalance = EmergencyBalance(
                        total = balance
                    )

                    lifecycleScope.launch {
                        emergencyExpensesDao.insert(emergencyExpense)
                        emergencyBalanceDao.insertOrUpdate(emergencyBalance)
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

        // Quick database test: print all expenses to Logcat
        lifecycleScope.launch {
            val expenses = emergencyExpensesDao.getAllNow() // Make sure getAllNow() is a suspend function that returns a List<Expense>
            for (expense in expenses) {
                Log.d("DB_TEST", expense.toString())
            }
        }

    }
}