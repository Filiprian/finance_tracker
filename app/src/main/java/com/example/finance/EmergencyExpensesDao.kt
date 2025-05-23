package com.example.finance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmergencyExpensesDao {
    @Insert
    suspend fun insert(emergencyExpense: EmergencyExpense)

    @Query("SELECT * FROM EmergencyExpense")
    suspend fun getAllNow(): List<EmergencyExpense>

    @Query("SELECT * FROM emergencyExpense ORDER BY year DESC, month DESC, day DESC")
    suspend fun getAllExpenses(): List<EmergencyExpense>

    @Query("SELECT SUM(value) FROM emergencyExpense")
    suspend fun getTotalBalance(): Int?


    @Delete
    suspend fun delete(expense: EmergencyExpense)
}