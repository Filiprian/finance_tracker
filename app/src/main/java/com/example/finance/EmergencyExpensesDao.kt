package com.example.finance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmergencyExpensesDao {
    @Insert
    suspend fun insert(emergencyExpense: EmergencyExpense)

    @Query("SELECT * FROM EmergencyExpense")
    suspend fun getAllNow(): List<EmergencyExpense>
}