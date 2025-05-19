package com.example.dacs3.ui.workspace

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.WorkspaceDetailData
import com.example.dacs3.data.repository.WorkspaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class WorkspaceDetailState(
    val isLoading: Boolean = false,
    val data: WorkspaceDetailData? = null,
    val error: String? = null
)

@HiltViewModel
class WorkspaceDetailViewModel @Inject constructor(
    private val workspaceRepository: WorkspaceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkspaceDetailState())
    val state: StateFlow<WorkspaceDetailState> = _state.asStateFlow()

    fun loadWorkspaceDetails(workspaceId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = workspaceRepository.getWorkspaceByIdFromApi(workspaceId)
                
                if (response.success && response.data != null) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            data = response.data
                        )
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load workspace details"
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e("WorkspaceDetailViewModel", "Network error loading workspace details", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Network error. Please check your connection."
                    )
                }
            } catch (e: HttpException) {
                Log.e("WorkspaceDetailViewModel", "HTTP error loading workspace details: ${e.code()}", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error ${e.code()}: ${e.message()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("WorkspaceDetailViewModel", "Error loading workspace details", e)
                _state.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error loading workspace details: ${e.message}"
                    )
                }
            }
        }
    }
}
