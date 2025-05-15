package com.example.finance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Expense::class, Balance::class, EmergencyExpense::class, EmergencyBalance::class, EmergencyGoal::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun balanceDao(): BalanceDao
    abstract fun emergencyBalanceDao(): EmergencyBalanceDao
    abstract fun emergencyExpenseDao(): EmergencyExpensesDao
    abstract fun emergencyGoalDao(): EmergencyGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_database"
                )
                    .fallbackToDestructiveMigration() // good for dev/testing
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}