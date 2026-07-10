package com.example.mywallet.data

import com.example.mywallet.dao.SubscriptionDao
import com.example.mywallet.data.model.Subscription
import kotlinx.coroutines.flow.Flow

class SubscriptionRepository(private val dao: SubscriptionDao) {
    val allActiveSubscriptions: Flow<List<Subscription>> = dao.getAllActive()

    suspend fun insert(subscription: Subscription) : Long{
        return dao.insert(subscription)
    }
    suspend fun update(subscription: Subscription){
        dao.update(subscription)
    }
    suspend fun delete(subscription: Subscription){
        dao.delete(subscription)
    }
    suspend fun getById(id: Long) : Subscription? {
        return dao.getById(id)
    }
}