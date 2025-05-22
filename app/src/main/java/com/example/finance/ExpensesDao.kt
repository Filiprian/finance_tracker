package com.example.finance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM Expense")
    suspend fun getAllNow(): List<Expense>

    @Query("SELECT * FROM Expense ORDER BY year DESC, month DESC, day DESC")
    suspend fun getAllExpenses(): List<Expense>

    @Delete
    suspend fun delete(expense: Expense)
}