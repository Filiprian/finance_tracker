package com.example.finance

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var balance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            val Balance = findViewById<TextView>(R.id.ttBalance)
            val Plus = findViewById<Button>(R.id.btPlus)
            val Minus = findViewById<Button>(R.id.btMinus)


            Plus.setOnClickListener {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.income, null)
                val dialogBuilder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)

                val Add = dialogView.findViewById<Button>(R.id.btAdd)
                val Value = dialogView.findViewById<EditText>(R.id.etValue)
                val Day = dialogView.findViewById<EditText>(R.id.etDay)
                val Month = dialogView.findViewById<EditText>(R.id.etMonth)
                val Year = dialogView.findViewById<EditText>(R.id.etYear)

                Add.text = "+"

                val alertDialog = dialogBuilder.create()
                alertDialog.show()

                Add.setOnClickListener {
                    val value = Value.text.toString().toIntOrNull()
                    val day = Day.text.toString().toIntOrNull()
                    val month = Month.text.toString().toIntOrNull()
                    val year = Year.text.toString().toIntOrNull()

                    if (value != null && day in 1..31 && month in 1..12 && year != null) {
                        balance += value
                        Balance.text = "$balance Kč"
                        alertDialog.dismiss()
                    }


                }
            }

            Minus.setOnClickListener {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.income, null)
                val dialogBuilder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)

                val Add = dialogView.findViewById<Button>(R.id.btAdd)
                val Value = dialogView.findViewById<EditText>(R.id.etValue)
                val Day = dialogView.findViewById<EditText>(R.id.etDay)
                val Month = dialogView.findViewById<EditText>(R.id.etMonth)
                val Year = dialogView.findViewById<EditText>(R.id.etYear)

                Add.text = "-"

                val alertDialog = dialogBuilder.create()
                alertDialog.show()

                Add.setOnClickListener {
                    val value = Value.text.toString().toIntOrNull()
                    val day = Day.text.toString().toIntOrNull()
                    val month = Month.text.toString().toIntOrNull()
                    val year = Year.text.toString().toIntOrNull()

                    if (value != null && day in 1..31 && month in 1..12 && year != null) {
                        balance -= value
                        Balance.text = "$balance Kč"
                        alertDialog.dismiss()
                    }


                }
            }



            insets
        }
    }
}