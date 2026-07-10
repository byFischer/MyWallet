package com.example.mywallet.dao

import androidx.room.*
import com.example.mywallet.data.model.Installment
import kotlinx.coroutines.flow.Flow

@Dao
interface InstallmentDao {

    @Insert
    suspend fun insert(installment: Installment): Long

    @Update
    suspend fun update(installment: Installment)

    @Delete
    suspend fun delete(installment: Installment)

    @Query("SELECT * FROM installments ORDER BY startDate DESC")
    fun getAll(): Flow<List<Installment>>

    @Query("SELECT * FROM installments WHERE id = :id")
    suspend fun getById(id: Long): Installment?
}