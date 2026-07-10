package com.example.mywallet.dao

import androidx.room.*
import com.example.mywallet.data.model.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun insert(subscription: Subscription) : Long

    @Update
    suspend fun update(subscription: Subscription)

    @Delete
    suspend fun delete(subscription: Subscription)

    @Query("SELECT * FROM subscriptions WHERE isActive = 1 ORDER BY nextPaymentDate ASC")
    fun getAllActive(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long) : Subscription?
}