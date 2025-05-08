package com.example.finance

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: Int,
    val day: Int,
    val month: Int,
    val year: Int
)

@Entity
data class Balance(
    @PrimaryKey val id: Int = 1,
    val total: Int
)