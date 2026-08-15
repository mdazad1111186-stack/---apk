package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expenses WHERE targetMonth = :month ORDER BY timestamp DESC")
    fun getExpensesByMonth(month: String): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Long): ExpenseTransaction?

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenseAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE targetMonth = :month")
    fun getTotalExpenseAmountForMonth(month: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseTransaction>)

    @Update
    suspend fun updateExpense(expense: ExpenseTransaction)

    @Delete
    suspend fun deleteExpense(expense: ExpenseTransaction)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("DELETE FROM expenses")
    suspend fun clearAllExpenses()
}
