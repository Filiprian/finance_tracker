package com.example.finance

import android.os.Bundle
import android.util.Log
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
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var expenseDao: ExpenseDao

    private lateinit var expenseAdapter: ExpenseAdapter

    private lateinit var balanceText: TextView
    private var balance = 0

    fun updateBalanceDisplay() {
        balanceText.text = "$balance Kč"
    }

    fun refreshBalance() {
        lifecycleScope.launch {
            val total = expenseDao.getTotalBalance() ?: 0
            balance = total
            updateBalanceDisplay()
        }
    }

    fun refreshExpenseList() {
        lifecycleScope.launch {
            val expense = expenseDao.getAllExpenses()
            expenseAdapter.updateData(expense)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        expenseDao = db.expenseDao()

        balanceText = findViewById(R.id.ttBalance)
        val plusButton = findViewById<Button>(R.id.btPlus)
        val minusButton = findViewById<Button>(R.id.btMinus)
        val rightButton = findViewById<Button>(R.id.btRight)
        var categories: List<String>

        // History of income/expenses
        val recyclerView = findViewById<RecyclerView>(R.id.History)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val expense = expenseDao.getAllExpenses()
            expenseAdapter = ExpenseAdapter(
                expense.toMutableList(),
                expenseDao,
                this@MainActivity
                )
            recyclerView.adapter = expenseAdapter
        }



        lifecycleScope.launch {
            val total = expenseDao.getTotalBalance() ?: 0
            balance = total
            updateBalanceDisplay()
        }

        rightButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
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

                    val expense = Expense(
                        value = finalValue,
                        day = day,
                        month = month,
                        year = year,
                        category = selectedCategory
                    )

                    lifecycleScope.launch {
                        expenseDao.insert(expense)
                        refreshBalance()
                        refreshExpenseList()
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
            val expenses = expenseDao.getAllNow() // Make sure getAllNow() is a suspend function that returns a List<Expense>
            for (expense in expenses) {
                Log.d("DB_TEST", expense.toString())
            }
        }
    }
}