package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.SprintApi
import com.example.dacs3.data.local.dao.SprintDao
import com.example.dacs3.data.local.entity.SprintEntity
import com.example.dacs3.data.model.AddItemsRequest
import com.example.dacs3.data.model.CreateSprintRequest
import com.example.dacs3.data.model.RemoveItemsRequest
import com.example.dacs3.data.model.SprintListResponse
import com.example.dacs3.data.model.SprintResponse
import com.example.dacs3.data.model.UpdateSprintRequest
import com.example.dacs3.data.repository.SprintRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SprintRepositoryImpl @Inject constructor(
    private val sprintDao: SprintDao,
    private val sprintApi: SprintApi
) : SprintRepository {
    
    private val TAG = "SprintRepositoryImpl"
    
    override fun getAll(): Flow<List<SprintEntity>> {
        return sprintDao.getAllSprints()
    }
    
    override suspend fun getById(id: String): SprintEntity? {
        return sprintDao.getSprintById(id)
    }
    
    override suspend fun insert(item: SprintEntity) {
        sprintDao.insertSprint(item)
    }
    
    override suspend fun insertAll(items: List<SprintEntity>) {
        sprintDao.insertSprints(items)
    }
    
    override suspend fun update(item: SprintEntity) {
        sprintDao.updateSprint(item)
    }
    
    override suspend fun delete(item: SprintEntity) {
        sprintDao.deleteSprint(item)
    }
    
    override suspend fun deleteById(id: String) {
        sprintDao.deleteSprintById(id)
    }
    
    override suspend fun deleteAll() {
        sprintDao.deleteAllSprints()
    }
    
    override suspend fun sync() {
        try {
            val response = sprintApi.getAllSprints()
            if (response.success && response.data != null) {
                val sprints = response.data.map { SprintEntity.fromSprint(it) }
                sprintDao.insertSprints(sprints)
                Log.d(TAG, "Successfully synced ${sprints.size} sprints")
            } else {
                Log.w(TAG, "Failed to sync sprints")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing sprints", e)
        }
    }
    
    override fun getSprintsByWorkspaceId(workspaceId: String): Flow<List<SprintEntity>> {
        return sprintDao.getSprintsByWorkspaceId(workspaceId)
    }
    
    override fun getSprintsByStatus(status: String): Flow<List<SprintEntity>> {
        return sprintDao.getSprintsByStatus(status)
    }
    
    override fun getSprintsByUserId(userId: String): Flow<List<SprintEntity>> {
        return sprintDao.getSprintsByUserId(userId)
    }
    
    override fun getActiveSprints(currentDate: Date): Flow<List<SprintEntity>> {
        return sprintDao.getActiveSprints(currentDate)
    }
    
    override suspend fun getAllSprintsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        status: String?
    ): SprintListResponse {
        return try {
            val response = sprintApi.getAllSprints(page, limit, workspaceId, status)
            
            // If successful, store sprints in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntities = response.data.map { SprintEntity.fromSprint(it) }
                    sprintDao.insertSprints(sprintEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching sprints from API", e)
            // Return empty response with success=false when API fails
            SprintListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getSprintsForCurrentUserFromApi(): SprintListResponse {
        return try {
            val response = sprintApi.getSprintByIdUser()
            
            // If successful, store sprints in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntities = response.data.map { SprintEntity.fromSprint(it) }
                    sprintDao.insertSprints(sprintEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching sprints for current user from API", e)
            // Return empty response with success=false when API fails
            SprintListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getSprintByIdFromApi(id: String): SprintResponse {
        return try {
            val response = sprintApi.getSprintById(id)
            
            // If successful, store sprint in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntity = SprintEntity.fromSprint(response.data)
                    sprintDao.insertSprint(sprintEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching sprint from API", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
    
    override suspend fun createSprint(
        name: String,
        description: String?,
        workspaceId: String,
        startDate: Date,
        endDate: Date,
        goal: String?,
        status: String?
    ): SprintResponse {
        return try {
            val request = CreateSprintRequest(
                name, description, workspaceId, startDate, endDate, goal, status
            )
            val response = sprintApi.createSprint(request)
            
            // If successful, store sprint in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntity = SprintEntity.fromSprint(response.data)
                    sprintDao.insertSprint(sprintEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating sprint", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
    
    override suspend fun updateSprint(
        id: String,
        name: String?,
        description: String?,
        startDate: Date?,
        endDate: Date?,
        goal: String?,
        status: String?
    ): SprintResponse {
        return try {
            val request = UpdateSprintRequest(
                name, description, startDate, endDate, goal, status
            )
            val response = sprintApi.updateSprint(id, request)
            
            // If successful, update sprint in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntity = SprintEntity.fromSprint(response.data)
                    sprintDao.updateSprint(sprintEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating sprint", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
    
    override suspend fun deleteSprintFromApi(id: String): Boolean {
        return try {
            val response = sprintApi.deleteSprint(id)
            
            // If successful, delete sprint from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    sprintDao.deleteSprintById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting sprint", e)
            false
        }
    }
    
    override suspend fun addItems(sprintId: String, tasks: List<String>?): SprintResponse {
        return try {
            val request = AddItemsRequest(tasks)
            val response = sprintApi.addItems(sprintId, request)
            
            // If successful, update sprint in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntity = SprintEntity.fromSprint(response.data)
                    sprintDao.updateSprint(sprintEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding items to sprint", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
    
    override suspend fun removeItems(sprintId: String, tasks: List<String>?): SprintResponse {
        return try {
            val request = RemoveItemsRequest(tasks)
            val response = sprintApi.removeItems(sprintId, request)
            
            // If successful, update sprint in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val sprintEntity = SprintEntity.fromSprint(response.data)
                    sprintDao.updateSprint(sprintEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error removing items from sprint", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
} 