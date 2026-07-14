package com.example.mywallet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mywallet.data.AppDatabase
import com.example.mywallet.data.SubscriptionRepository
import com.example.mywallet.data.model.Subscription
import com.example.mywallet.data.remote.BrandSearchResult
import com.example.mywallet.data.remote.BrandfetchClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BrandSearchUiState(
    val results: List<BrandSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val isConfigured: Boolean = true,
    val hasError: Boolean = false
)

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : SubscriptionRepository
    private val brandfetchClient = BrandfetchClient()
    private var brandSearchJob: Job? = null

    private val _brandSearchState = MutableStateFlow(
        BrandSearchUiState(isConfigured = brandfetchClient.isConfigured)
    )
    val brandSearchState: StateFlow<BrandSearchUiState> = _brandSearchState.asStateFlow()

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

    fun searchBrands(query: String) {
        brandSearchJob?.cancel()
        val cleanQuery = query.trim()

        if (cleanQuery.length < 2 || !brandfetchClient.isConfigured) {
            _brandSearchState.value = BrandSearchUiState(
                isConfigured = brandfetchClient.isConfigured
            )
            return
        }

        _brandSearchState.value = BrandSearchUiState(isConfigured = true)

        brandSearchJob = viewModelScope.launch {
            delay(350)
            _brandSearchState.value = BrandSearchUiState(
                isLoading = true,
                isConfigured = true
            )

            try {
                val results = brandfetchClient.search(cleanQuery).take(6)
                _brandSearchState.value = BrandSearchUiState(
                    results = results,
                    hasSearched = true,
                    isConfigured = true
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _brandSearchState.value = BrandSearchUiState(
                    hasSearched = true,
                    isConfigured = true,
                    hasError = true
                )
            }
        }
    }

    fun clearBrandSearch() {
        brandSearchJob?.cancel()
        _brandSearchState.value = BrandSearchUiState(
            isConfigured = brandfetchClient.isConfigured
        )
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
