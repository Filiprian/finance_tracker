package com.example.finance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface EmergencyGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(emergencyGoal: EmergencyGoal)

    @Query("SELECT * FROM EmergencyGoal WHERE id = 1")
    suspend fun getBalance(): EmergencyGoal?
}