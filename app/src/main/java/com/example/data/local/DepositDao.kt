package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DepositTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY timestamp DESC, id DESC")
    fun getAllDeposits(): Flow<List<DepositTransaction>>

    @Query("SELECT * FROM deposits WHERE memberId = :memberId ORDER BY timestamp DESC")
    fun getDepositsByMember(memberId: Long): Flow<List<DepositTransaction>>

    @Query("SELECT * FROM deposits WHERE targetMonth = :month ORDER BY timestamp DESC")
    fun getDepositsByMonth(month: String): Flow<List<DepositTransaction>>

    @Query("SELECT * FROM deposits WHERE id = :id")
    suspend fun getDepositById(id: Long): DepositTransaction?

    @Query("SELECT SUM(amount) FROM deposits")
    fun getTotalDepositAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM deposits WHERE targetMonth = :month")
    fun getTotalDepositAmountForMonth(month: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM deposits WHERE memberId = :memberId")
    fun getTotalDepositByMember(memberId: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposits(deposits: List<DepositTransaction>)

    @Update
    suspend fun updateDeposit(deposit: DepositTransaction)

    @Delete
    suspend fun deleteDeposit(deposit: DepositTransaction)

    @Query("DELETE FROM deposits WHERE id = :id")
    suspend fun deleteDepositById(id: Long)

    @Query("DELETE FROM deposits")
    suspend fun clearAllDeposits()
}
