package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.WorkspaceApi
import com.example.dacs3.data.local.dao.WorkspaceDao
import com.example.dacs3.data.local.entity.WorkspaceEntity
import com.example.dacs3.data.model.*
import com.example.dacs3.data.repository.WorkspaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val workspaceApi: WorkspaceApi
) : WorkspaceRepository {
    
    private val TAG = "WorkspaceRepositoryImpl"
    
    override fun getAll(): Flow<List<WorkspaceEntity>> {
        return workspaceDao.getAllWorkspaces()
    }
    
    override suspend fun getById(id: String): WorkspaceEntity? {
        return workspaceDao.getWorkspaceById(id)
    }
    
    override suspend fun insert(item: WorkspaceEntity) {
        workspaceDao.insertWorkspace(item)
    }
    
    override suspend fun insertAll(items: List<WorkspaceEntity>) {
        workspaceDao.insertWorkspaces(items)
    }
    
    override suspend fun update(item: WorkspaceEntity) {
        workspaceDao.updateWorkspace(item)
    }
    
    override suspend fun delete(item: WorkspaceEntity) {
        workspaceDao.deleteWorkspace(item)
    }
    
    override suspend fun deleteById(id: String) {
        workspaceDao.deleteWorkspaceById(id)
    }
    
    override suspend fun deleteAll() {
        workspaceDao.deleteAllWorkspaces()
    }
    
    override suspend fun sync() {
        try {
            val response = workspaceApi.getAllWorkspaces()
            if (response.success && response.data != null) {
                val workspaces = response.data.map { WorkspaceEntity.fromWorkspace(it) }
                workspaceDao.insertWorkspaces(workspaces)
                Log.d(TAG, "Successfully synced ${workspaces.size} workspaces")
            } else {
                Log.w(TAG, "Failed to sync workspaces")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing workspaces", e)
        }
    }
    
    override fun getWorkspacesByUserId(userId: String): Flow<List<WorkspaceEntity>> {
        return workspaceDao.getWorkspacesByUserId(userId)
    }
    
    override suspend fun getAllWorkspacesFromApi(page: Int?, limit: Int?): WorkspaceListResponse {
        return try {
            val response = workspaceApi.getAllWorkspaces(page, limit)
            
            // If successful, store workspaces in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntities = response.data.map { WorkspaceEntity.fromWorkspace(it) }
                    workspaceDao.insertWorkspaces(workspaceEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching workspaces from API", e)
            // Return empty response with success=false when API fails
            WorkspaceListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getAllWorkspacesByUserIdFromApi(userId: String, page: Int?, limit: Int?): WorkspaceListResponse {
        return try {
            val response = workspaceApi.getAllWorkspacesByUserId(userId, page, limit)
            
            // If successful, store workspaces in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntities = response.data.map { WorkspaceEntity.fromWorkspace(it) }
                    workspaceDao.insertWorkspaces(workspaceEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching workspaces by user ID from API", e)
            // Return empty response with success=false when API fails
            WorkspaceListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getWorkspaceByIdFromApi(id: String): WorkspaceResponse {
        return try {
            val response = workspaceApi.getWorkspaceById(id)
            
            // If successful, store workspace in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntity = WorkspaceEntity.fromWorkspace(response.data)
                    workspaceDao.insertWorkspace(workspaceEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching workspace from API", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun createWorkspace(name: String, description: String?): WorkspaceResponse {
        return try {
            val request = CreateWorkspaceRequest(name, description)
            val response = workspaceApi.createWorkspace(request)
            
            // If successful, store workspace in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntity = WorkspaceEntity.fromWorkspace(response.data)
                    workspaceDao.insertWorkspace(workspaceEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating workspace", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun updateWorkspace(id: String, name: String?, description: String?): WorkspaceResponse {
        return try {
            val request = UpdateWorkspaceRequest(name, description)
            val response = workspaceApi.updateWorkspace(id, request)
            
            // If successful, update workspace in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntity = WorkspaceEntity.fromWorkspace(response.data)
                    workspaceDao.updateWorkspace(workspaceEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating workspace", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun deleteWorkspaceFromApi(id: String): Boolean {
        return try {
            val response = workspaceApi.deleteWorkspace(id)
            
            // If successful, delete workspace from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    workspaceDao.deleteWorkspaceById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting workspace", e)
            false
        }
    }
    
    override suspend fun addMember(workspaceId: String, userId: String, role: String?): WorkspaceResponse {
        return try {
            val request = AddMemberRequest(userId, role)
            val response = workspaceApi.addMember(workspaceId, request)
            
            // If successful, update workspace in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntity = WorkspaceEntity.fromWorkspace(response.data)
                    workspaceDao.updateWorkspace(workspaceEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding member to workspace", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun removeMember(workspaceId: String, userId: String): WorkspaceResponse {
        return try {
            val response = workspaceApi.removeMember(workspaceId, userId)
            
            // If successful, update workspace in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntity = WorkspaceEntity.fromWorkspace(response.data)
                    workspaceDao.updateWorkspace(workspaceEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error removing member from workspace", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun joinWorkspace(workspaceId: String): WorkspaceResponse {
        return try {
            val response = workspaceApi.joinWorkspace(workspaceId)
            
            // If successful, update workspace in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val workspaceEntity = WorkspaceEntity.fromWorkspace(response.data)
                    workspaceDao.insertWorkspace(workspaceEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error joining workspace", e)
            // Return empty response with success=false when API fails
            WorkspaceResponse(false, null)
        }
    }
    
    override suspend fun leaveWorkspace(workspaceId: String): Boolean {
        return try {
            val response = workspaceApi.leaveWorkspace(workspaceId)
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error leaving workspace", e)
            false
        }
    }
    
    override suspend fun getWorkspaceMembersFromApi(workspaceId: String): UserListResponse {
        return try {
            val response = workspaceApi.getWorkspaceMembers(workspaceId)
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error getting workspace members", e)
            // Return empty response with success=false when API fails
            UserListResponse(false, 0, 0, emptyList())
        }
    }
} 