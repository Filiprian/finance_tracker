package com.example.finance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface EmergencyBalanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(emergencyBalance: EmergencyBalance)

    @Query("SELECT * FROM EmergencyBalance WHERE id = 1")
    suspend fun getBalance(): EmergencyBalance?
}