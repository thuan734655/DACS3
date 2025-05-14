package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.DailyReport
import com.example.dacs3.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _reports = MutableStateFlow<List<DailyReport>>(emptyList())
    val reports: StateFlow<List<DailyReport>> = _reports

    private val _currentReport = MutableStateFlow<DailyReport?>(null)
    val currentReport: StateFlow<DailyReport?> = _currentReport

    private val _statistics = MutableStateFlow<Any?>(null)
    val statistics: StateFlow<Any?> = _statistics

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getDailyReports(workspaceId: String? = null, userId: String? = null, date: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getDailyReports(workspaceId, userId, date)
                if (response.isSuccessful && response.body()?.success == true) {
                    _reports.value = response.body()?.data ?: emptyList()
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

    fun getDailyReportById(reportId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getDailyReportById(reportId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _currentReport.value = response.body()?.data
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

    fun createDailyReport(report: DailyReport) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.createDailyReport(report)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh reports list after creation
                    getDailyReports(report.workspaceId)
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

    fun updateDailyReport(reportId: String, report: DailyReport) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.updateDailyReport(reportId, report)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Update current report if it's the one being edited
                    if (_currentReport.value?.id == reportId) {
                        _currentReport.value = response.body()?.data
                    }
                    // Refresh reports list after update
                    getDailyReports(report.workspaceId)
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

    fun deleteDailyReport(reportId: String, workspaceId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteDailyReport(reportId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Clear current report if it's the one being deleted
                    if (_currentReport.value?.id == reportId) {
                        _currentReport.value = null
                    }
                    // Refresh reports list after deletion
                    getDailyReports(workspaceId)
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

    fun getReportStatistics(workspaceId: String, startDate: String? = null, endDate: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getReportStatistics(workspaceId, startDate, endDate)
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
} 