package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.MockData
import com.example.dacs3.models.HomeResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val homeResponse: HomeResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(isLoading = true)
            try {
                // In a real app, this would be an API call
                _homeState.value = HomeState(homeResponse = MockData.mockHomeResponse)
            } catch (e: Exception) {
                _homeState.value = _homeState.value.copy(error = e.message)
            } finally {
                _homeState.value = _homeState.value.copy(isLoading = false)
            }
        }
    }

    fun selectChannel(channelId: String) {
        // TODO: Implement channel selection logic
    }

    fun sendMessage(content: String) {
        // TODO: Implement message sending logic
    }
}
