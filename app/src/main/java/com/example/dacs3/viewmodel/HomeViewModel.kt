package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.models.HomeResponse
import com.example.dacs3.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repo: AuthRepository): ViewModel() {
    private val _homeState = MutableStateFlow<UiState<HomeResponse>>(UiState.Idle)
    val homeState: StateFlow<UiState<HomeResponse>> = _homeState

    init { fetchHome() }

    fun fetchHome() = viewModelScope.launch {
        _homeState.value = UiState.Loading
        try {
            val response: Response<HomeResponse> = repo.getHomeData("Bearer ${getTokenFromPrefs()}")
            if (response.isSuccessful) {
                response.body()?.let {
                    _homeState.value = UiState.Success(it)
                } ?: run {
                    _homeState.value = UiState.Error("Response body is null")
                }
            } else {
                _homeState.value = UiState.Error("Error ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            _homeState.value = UiState.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    private fun getTokenFromPrefs(): String {
        return "" // TODO: Lấy token từ SharedPreferences hoặc DataStore
    }
}
