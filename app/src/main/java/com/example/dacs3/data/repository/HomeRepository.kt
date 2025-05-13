package com.example.dacs3.data.repository

import com.example.dacs3.data.api.HomeApi
import com.example.dacs3.data.model.Channel
import com.example.dacs3.data.model.Notification
import com.example.dacs3.data.model.Workspace
import com.example.dacs3.data.session.SessionManager
import com.example.dacs3.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val homeApi: HomeApi,
    private val sessionManager: SessionManager
) {
    // Format token header
    private fun getTokenHeader(): String {
        val token = sessionManager.getAuthToken() ?: ""
        return "Bearer $token"
    }

    // Lấy danh sách workspace
    fun getWorkspaces(): Flow<Resource<List<Workspace>>> = flow {
        emit(Resource.Loading())
        
        val response = homeApi.getWorkspaces(getTokenHeader())
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { workspaces ->
                emit(Resource.Success(workspaces))
            } ?: emit(Resource.Error("No workspaces found"))
        } else {
            emit(Resource.Error(response.body()?.message ?: "Unknown error occurred"))
        }
    }.catch { e ->
        when(e) {
            is HttpException -> emit(Resource.Error("Network error: ${e.message}"))
            is IOException -> emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            else -> emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }
    
    // Lấy thông tin workspace
    fun getWorkspace(workspaceId: String): Flow<Resource<Workspace>> = flow {
        emit(Resource.Loading())
        
        val response = homeApi.getWorkspace(getTokenHeader(), workspaceId)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { workspace ->
                emit(Resource.Success(workspace))
            } ?: emit(Resource.Error("Workspace not found"))
        } else {
            emit(Resource.Error(response.body()?.message ?: "Unknown error occurred"))
        }
    }.catch { e ->
        when(e) {
            is HttpException -> emit(Resource.Error("Network error: ${e.message}"))
            is IOException -> emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            else -> emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }
    
    // Lấy danh sách channel trong workspace
    fun getChannels(workspaceId: String): Flow<Resource<List<Channel>>> = flow {
        emit(Resource.Loading())
        
        val response = homeApi.getChannels(getTokenHeader(), workspaceId)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { channels ->
                emit(Resource.Success(channels))
            } ?: emit(Resource.Error("No channels found"))
        } else {
            emit(Resource.Error(response.body()?.message ?: "Unknown error occurred"))
        }
    }.catch { e ->
        when(e) {
            is HttpException -> emit(Resource.Error("Network error: ${e.message}"))
            is IOException -> emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            else -> emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }
    
    // Lấy danh sách notification theo workspace
    fun getNotifications(workspaceId: String): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        
        val response = homeApi.getNotificationsByWorkspace(getTokenHeader(), workspaceId)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.let { notifications ->
                emit(Resource.Success(notifications))
            } ?: emit(Resource.Error("No notifications found"))
        } else {
            emit(Resource.Error(response.body()?.message ?: "Unknown error occurred"))
        }
    }.catch { e ->
        when(e) {
            is HttpException -> emit(Resource.Error("Network error: ${e.message}"))
            is IOException -> emit(Resource.Error("Couldn't reach server. Check your internet connection"))
            else -> emit(Resource.Error("Unknown error: ${e.message}"))
        }
    }
} 