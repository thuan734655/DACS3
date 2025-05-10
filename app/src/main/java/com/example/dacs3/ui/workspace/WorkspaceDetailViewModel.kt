package com.example.dacs3.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dacs3.data.local.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceDetailViewModel @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val channelDao: ChannelDao,
    private val userDao: UserDao,
    private val workspaceUserMembershipDao: WorkspaceUserMembershipDao
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    
    val workspace = _workspaceId.filterNotNull().flatMapLatest { id ->
        flow {
            val workspace = workspaceDao.getWorkspaceById(id)
            emit(workspace)
        }
    }
    
    val channels = _workspaceId.filterNotNull().flatMapLatest { id ->
        channelDao.getChannelsByWorkspaceId(id)
    }
    
    val members = _workspaceId.filterNotNull().flatMapLatest { id ->
        workspaceUserMembershipDao.getMembersByWorkspaceId(id)
            .combine(userDao.getAllUsers()) { memberships, users ->
                val memberIds = memberships.map { it.userId }
                users.filter { it.userId in memberIds }
            }
    }
    
    fun setWorkspaceId(id: String) {
        _workspaceId.value = id
        loadWorkspaceData(id)
    }
    
    private fun loadWorkspaceData(workspaceId: String) {
        viewModelScope.launch {
            // No longer load sample data - just load actual data from the database
            // The user doesn't want mock data
        }
    }
} 