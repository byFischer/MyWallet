package com.example.mywallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywallet.data.AppDatabase
import com.example.mywallet.data.SubscriptionRepository
import com.example.mywallet.data.model.Subscription
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : SubscriptionRepository
    val activeSubscriptions : StateFlow<List<Subscription>>

    init {
        val dao = AppDatabase.getDatabase(application).subscriptionDao()
        repository = SubscriptionRepository(dao)

        activeSubscriptions = repository.allActiveSubscriptions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addSubscription(subscription: Subscription){
        viewModelScope.launch {
            repository.insert(subscription)
        }
    }
    fun updateSubscription(subscription: Subscription){
        viewModelScope.launch {
            repository.update(subscription)
        }
    }
    fun deleteSubscription(subscription: Subscription){
        viewModelScope.launch {
            repository.delete(subscription)
        }
    }
    suspend fun getSubscriptionById(id: Long): Subscription? {
        return repository.getById(id)
    }
}