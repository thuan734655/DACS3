package com.example.dacs3.data.repository

import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.network.WorkspaceApi
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for workspace repository operations
 */
interface WorkspaceRepository {
    /**
     * Create a new workspace
     * @param name Name of the workspace
     * @param description Optional description of the workspace
     * @return Flow of Resource with created Workspace object
     */
    suspend fun createWorkspace(name: String, description: String): Resource<Workspace>
    
    /**
     * Get all workspaces for the current user
     * @return Flow of Resource with list of Workspace objects
     */
    fun getWorkspaces(): Flow<Resource<List<Workspace>>>
}

/**
 * Implementation of WorkspaceRepository
 */
@Singleton
class WorkspaceRepositoryImpl @Inject constructor(
    private val workspaceApi: WorkspaceApi,
    private val sessionManager: SessionManager
) : WorkspaceRepository {
    
    override suspend fun createWorkspace(name: String, description: String): Resource<Workspace> {
        return try {
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                return Resource.Error("Authentication token not found")
            }
            
            val request = mapOf(
                "name" to name,
                "description" to description
            )
            
            val response = workspaceApi.createWorkspace("Bearer $token", request)
            if (response.success && response.data != null) {
                Resource.Success(response.data)
            } else {
                Resource.Error(response.message ?: "Failed to create workspace")
            }
        } catch (e: HttpException) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        } catch (e: IOException) {
            Resource.Error("Couldn't reach server. Check your internet connection")
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
    
    override fun getWorkspaces(): Flow<Resource<List<Workspace>>> = flow {
        emit(Resource.Loading())
        
        try {
            val token = sessionManager.getAuthToken()
            if (token.isNullOrBlank()) {
                emit(Resource.Error("Authentication token not found"))
                return@flow
            }
            
            val response = workspaceApi.getWorkspaces("Bearer $token")
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message ?: "Failed to get workspaces"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
} 