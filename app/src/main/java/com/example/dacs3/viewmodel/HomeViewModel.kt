package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Dashboard
import com.example.dacs3.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _dashboard = MutableStateFlow<Dashboard?>(null)
    val dashboard: StateFlow<Dashboard?> = _dashboard

    private val _recentActivities = MutableStateFlow<Any?>(null)
    val recentActivities: StateFlow<Any?> = _recentActivities

    private val _statistics = MutableStateFlow<Any?>(null)
    val statistics: StateFlow<Any?> = _statistics

    private val _workspaceOverview = MutableStateFlow<Any?>(null)
    val workspaceOverview: StateFlow<Any?> = _workspaceOverview

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getDashboard() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getDashboard()
                if (response.isSuccessful && response.body()?.success == true) {
                    _dashboard.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getRecentActivities(page: Int? = null, limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getRecentActivities(page, limit)
                if (response.isSuccessful && response.body()?.success == true) {
                    _recentActivities.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getStatistics() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getStatistics()
                if (response.isSuccessful && response.body()?.success == true) {
                    _statistics.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getWorkspaceOverview(workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getWorkspaceOverview(workspaceId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _workspaceOverview.value = response.body()?.data
                } else {
                    _error.value = response.body()?.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _loading.value = false
            }
        }
    }
} 