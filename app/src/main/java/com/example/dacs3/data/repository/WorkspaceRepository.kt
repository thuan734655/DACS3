package com.example.dacs3.data.repository

import android.util.Log
import com.example.dacs3.data.api.WorkspaceApi
import com.example.dacs3.data.model.ApiResponse
import com.example.dacs3.data.model.Workspace
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepository @Inject constructor(
    private val api: WorkspaceApi
) {
    suspend fun getAllWorkspaces(): Response<ApiResponse<List<Workspace>>> {
        return try {
            api.getAllWorkspaces()
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error getting all workspaces", e)
            throw e
        }
    }
    
    suspend fun getWorkspaceById(workspaceId: String): Response<ApiResponse<Workspace>> {
        return try {
            api.getWorkspaceById(workspaceId)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error getting workspace by id", e)
            throw e
        }
    }
    
    suspend fun createWorkspace(workspace: Workspace): Response<ApiResponse<Workspace>> {
        return try {
            api.createWorkspace(workspace)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error creating workspace", e)
            throw e
        }
    }
    
    suspend fun updateWorkspace(workspaceId: String, workspace: Workspace): Response<ApiResponse<Workspace>> {
        return try {
            api.updateWorkspace(workspaceId, workspace)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error updating workspace", e)
            throw e
        }
    }
    
    suspend fun deleteWorkspace(workspaceId: String): Response<ApiResponse<Any>> {
        return try {
            api.deleteWorkspace(workspaceId)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error deleting workspace", e)
            throw e
        }
    }
    
    suspend fun addMember(workspaceId: String, memberId: String, role: String): Response<ApiResponse<Workspace>> {
        return try {
            val memberData = mapOf(
                "userId" to memberId,
                "role" to role
            )
            api.addMember(workspaceId, memberData)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error adding member to workspace", e)
            throw e
        }
    }
    
    suspend fun removeMember(workspaceId: String, memberId: String): Response<ApiResponse<Workspace>> {
        return try {
            api.removeMember(workspaceId, memberId)
        } catch (e: Exception) {
            Log.e("WorkspaceRepository", "Error removing member from workspace", e)
            throw e
        }
    }
} 