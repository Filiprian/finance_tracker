package com.example.finance

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {



    private var balance = 0

    private lateinit var db: AppDatabase
    private lateinit var emergencyExpensesDao: EmergencyExpensesDao
    private lateinit var emergencyGoalDao: EmergencyGoalDao

    private lateinit var balanceText: TextView
    private lateinit var warningText: TextView
    private lateinit var plusButton: Button
    private lateinit var minusButton: Button
    private lateinit var rightButton: Button
    private lateinit var leftButton: Button
    private lateinit var goalText: EditText
    private lateinit var progressBar: ProgressBar

    fun updateBalanceDisplay() {
        balanceText.text = "$balance Kč"
        val goal = goalText.text.toString().toFloatOrNull()
        if (goal != null && goal > 0) {
            val progress = ((balance.toFloat() / goal) * 100).toInt()
            progressBar.progress = progress.coerceIn(0, 100)
        }
    }

    fun refreshBalance() {
        lifecycleScope.launch {
            val total = emergencyExpensesDao.getTotalBalance() ?: 0
            balance = total
            updateBalanceDisplay()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        db = AppDatabase.getDatabase(this)
        emergencyExpensesDao = db.emergencyExpenseDao()
        emergencyGoalDao = db.emergencyGoalDao()


        balanceText = findViewById(R.id.ttBalance)
        warningText = findViewById(R.id.ttWarning)
        plusButton = findViewById(R.id.btPlus)
        minusButton = findViewById(R.id.btMinus)
        rightButton = findViewById(R.id.btRight)
        leftButton = findViewById(R.id.btLeft)
        goalText = findViewById(R.id.ttGoal)
        progressBar = findViewById(R.id.progressBar)
        var categories: List<String>

        // History of income/expenses
        val recyclerView = findViewById<RecyclerView>(R.id.History)
        recyclerView.layoutManager = LinearLayoutManager(this)


        lifecycleScope.launch {
            val emergencyExpense = emergencyExpensesDao.getAllExpenses()
            val adapter = EmergencyExpenseAdapter(
                emergencyExpense.toMutableList(),
                emergencyExpensesDao,
                this@SecondActivity
            )

            recyclerView.adapter = adapter
        }


        leftButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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
                        Log.d("DB_TEST", "Saved goal: ${emergencyGoal.total}")
                    }

                } else {
                    warningText.visibility = View.VISIBLE
                }

                updateBalanceDisplay()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        lifecycleScope.launch {
            val savedGoals = db.emergencyGoalDao().getBalance()
            goalText.setText("${savedGoals?.total ?: 0}")
        }

        lifecycleScope.launch {
            val total = emergencyExpensesDao.getTotalBalance() ?: 0
            balance = total
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

            if (isAddition) {
                actionButton.text = "+"
                categories = listOf("Job", "Investing", "Gift", "Others")
            }
            else {
                actionButton.text = "-"
                categories = listOf("Investing", "Education", "Rent", "Food", "Utilities", "Cloths", "Fun", "Other")
            }

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            val spinner = dialogView.findViewById<Spinner>(R.id.spinner)


            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
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

                    val selectedCategory = spinner.selectedItem.toString()

                    val emergencyExpense = EmergencyExpense(
                        value = finalValue,
                        day = day,
                        month = month,
                        year = year,
                        category = selectedCategory
                    )

                    lifecycleScope.launch {
                        emergencyExpensesDao.insert(emergencyExpense)
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