package com.example.mywallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywallet.data.AppDatabase
import com.example.mywallet.data.InstallmentRepository
import com.example.mywallet.data.SubscriptionRepository
import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription
import com.example.mywallet.domain.ExpenseCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val subscriptionRepository: SubscriptionRepository
    private val installmentRepository: InstallmentRepository

    val totalMonthlyExpense: StateFlow<Double>
    val activeSubscriptions: StateFlow<List<Subscription>>
    val allInstallments: StateFlow<List<Installment>>

    init {
        val db = AppDatabase.getDatabase(application)
        subscriptionRepository = SubscriptionRepository(db.subscriptionDao())
        installmentRepository = InstallmentRepository(db.installmentDao())

        activeSubscriptions = subscriptionRepository.allActiveSubscriptions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allInstallments = installmentRepository.allInstallments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalMonthlyExpense = combine(
            subscriptionRepository.allActiveSubscriptions,
            installmentRepository.allInstallments
        ) { subscriptions, installments ->
            val subscriptionTotal = subscriptions.sumOf { ExpenseCalculator.monthlyAmount(it) }
            val installmentTotal = installments
                .filter { ExpenseCalculator.isInstallmentActive(it) }
                .sumOf { ExpenseCalculator.monthlyAmount(it) }
            subscriptionTotal + installmentTotal
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch { subscriptionRepository.update(subscription) }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch { subscriptionRepository.delete(subscription) }
    }

    fun updateInstallment(installment: Installment) {
        viewModelScope.launch { installmentRepository.update(installment) }
    }

    fun deleteInstallment(installment: Installment) {
        viewModelScope.launch { installmentRepository.delete(installment) }
    }
}