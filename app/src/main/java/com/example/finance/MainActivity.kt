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
            var balance = 0

            Balance.text = "$balance Kč"

            Plus.setOnClickListener {
                val Add = findViewById<Button>(R.id.btAdd)
                val dialogView = LayoutInflater.from(this).inflate(R.layout.income, null)
                val dialogBuilder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(true)

                val alertDialog = dialogBuilder.create()
                alertDialog.show()

                Add.setOnClickListener {
                    val Value = findViewById<EditText>(R.id.etValue)
                    val value = Value.text.toString().toInt()
                    balance += value
                    Balance.text = "$balance Kč"
                    alertDialog.dismiss()


                }
            }



            insets
        }
    }
}