package com.example.mywallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywallet.data.AppDatabase
import com.example.mywallet.data.InstallmentRepository
import com.example.mywallet.data.model.Installment
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InstallmentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : InstallmentRepository
    val allInstallments: StateFlow<List<Installment>>

    init {
        val dao = AppDatabase.getDatabase(application).installmentDao()
        repository = InstallmentRepository(dao)

        allInstallments = repository.allInstallments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addInstallment(installment: Installment){
        viewModelScope.launch {
            repository.insert(installment)
        }
    }

    fun updateInstallment(installment: Installment){
        viewModelScope.launch {
            repository.update(installment)
        }
    }

    fun deleteInstallment(installment: Installment) {
        viewModelScope.launch {
            repository.delete(installment)
        }
    }
}