package com.example.dacs3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.model.Message
import com.example.dacs3.data.repository.MessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: MessagesRepository
) : ViewModel() {

    private val _channelMessages = MutableStateFlow<List<Message>>(emptyList())
    val channelMessages: StateFlow<List<Message>> = _channelMessages

    private val _directMessages = MutableStateFlow<List<Message>>(emptyList())
    val directMessages: StateFlow<List<Message>> = _directMessages

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getChannelMessages(channelId: String, page: Int? = null, limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getChannelMessages(channelId, page, limit)
                if (response.isSuccessful && response.body()?.success == true) {
                    _channelMessages.value = response.body()?.data ?: emptyList()
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

    fun getDirectMessages(userId: String, page: Int? = null, limit: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getDirectMessages(userId, page, limit)
                if (response.isSuccessful && response.body()?.success == true) {
                    _directMessages.value = response.body()?.data ?: emptyList()
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

    fun sendChannelMessage(channelId: String, message: Message) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.sendChannelMessage(channelId, message)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh channel messages after sending a new message
                    getChannelMessages(channelId)
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

    fun sendDirectMessage(userId: String, message: Message) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.sendDirectMessage(userId, message)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh direct messages after sending a new message
                    getDirectMessages(userId)
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

    fun deleteMessage(messageId: String, isChannelMessage: Boolean, refreshId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.deleteMessage(messageId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh messages based on the type
                    if (isChannelMessage) {
                        getChannelMessages(refreshId)
                    } else {
                        getDirectMessages(refreshId)
                    }
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

    fun editMessage(messageId: String, message: Message, isChannelMessage: Boolean, refreshId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.editMessage(messageId, message)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Refresh messages based on the type
                    if (isChannelMessage) {
                        getChannelMessages(refreshId)
                    } else {
                        getDirectMessages(refreshId)
                    }
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