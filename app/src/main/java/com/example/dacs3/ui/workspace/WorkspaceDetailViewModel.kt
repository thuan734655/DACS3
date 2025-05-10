package com.example.dacs3.ui.workspace

import android.util.Log
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
    private val workspaceUserMembershipDao: WorkspaceUserMembershipDao,
    private val epicDao: EpicDao,
    private val taskDao: TaskDao
) : ViewModel() {
    
    private val _workspaceId = MutableStateFlow<String?>(null)
    
    // For task count
    private val _taskCount = MutableStateFlow(0)
    val taskCount: StateFlow<Int> = _taskCount.asStateFlow()
    
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
    
    val epics = _workspaceId.filterNotNull().flatMapLatest { id ->
        epicDao.getEpicsByWorkspace(id)
    }
    
    // Set up the combined flow for tasks count
    init {
        _workspaceId
            .filterNotNull()
            .flatMapLatest { workspaceId ->
                epicDao.getEpicsByWorkspace(workspaceId)
                    .map { epics -> epics.map { it.epicId } }
            }
            .flatMapLatest { epicIds ->
                if (epicIds.isEmpty()) {
                    // If no epics, then no tasks
                    flow { emit(0) }
                } else {
                    // Watch task count for each epic
                    combine(
                        epicIds.map { epicId ->
                            taskDao.getTasksByEpic(epicId).map { it.size }
                        }
                    ) { counts ->
                        counts.sum()
                    }
                }
            }
            .onEach { count ->
                _taskCount.value = count
            }
            .catch { e ->
                Log.e("WorkspaceViewModel", "Error counting tasks", e)
                _taskCount.value = 0
            }
            .launchIn(viewModelScope)
    }
    
    fun setWorkspaceId(id: String) {
        _workspaceId.value = id
    }
} 