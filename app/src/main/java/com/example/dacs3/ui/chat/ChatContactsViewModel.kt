package com.example.dacs3.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.ChatMember
import com.example.dacs3.data.repository.impl.ChatRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatContactsViewModel @Inject constructor(
    private val chatRepository: ChatRepositoryImpl
) : ViewModel() {

    private val _state = MutableStateFlow(ChatContactsUiState())
    val state: StateFlow<ChatContactsUiState> = _state.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts(page: Int = 1) {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true,
                error = ""
            )}

            try {
                val result = chatRepository.getUsersInSameWorkspaces(page)
                result.fold(
                    onSuccess = { members ->
                        _state.update { it.copy(
                            isLoading = false,
                            contacts = if (page == 1) members else it.contacts + members
                        )}
                    },
                    onFailure = { error ->
                        Log.e("ChatContactsViewModel", "Failed to load contacts", error)
                        _state.update { it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load contacts"
                        )}
                    }
                )
            } catch (e: Exception) {
                Log.e("ChatContactsViewModel", "Error loading contacts", e)
                _state.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )}
            }
        }
    }

    fun searchContacts(query: String) {
        val filteredContacts = _state.value.allContacts.filter { 
            it.user?.name?.contains(query, ignoreCase = true) == true 
        }
        _state.update { it.copy(contacts = filteredContacts) }
    }

    fun refreshContacts() {
        loadContacts(1)
    }
}

data class ChatContactsUiState(
    val isLoading: Boolean = false,
    val contacts: List<ChatMember> = emptyList(),
    val allContacts: List<ChatMember> = emptyList(),
    val error: String = ""
)
