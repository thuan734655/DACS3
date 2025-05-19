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
        TODO()
    }
    
    override suspend fun getById(id: String): SprintEntity? {
        TODO()
    }
    
    override suspend fun insert(item: SprintEntity) {
        TODO()
    }
    
    override suspend fun insertAll(items: List<SprintEntity>) {
        TODO()
    }
    
    override suspend fun update(item: SprintEntity) {
        TODO()
    }
    
    override suspend fun delete(item: SprintEntity) {
        TODO()
    }
    
    override suspend fun deleteById(id: String) {
        TODO()
    }
    
    override suspend fun deleteAll() {
        TODO()
    }
    
    override suspend fun sync() {
        TODO()
    }
    
    override fun getSprintsByWorkspaceId(workspaceId: String): Flow<List<SprintEntity>> {
        TODO()
    }
    
    override fun getSprintsByStatus(status: String): Flow<List<SprintEntity>> {
        TODO()
    }
    
    override fun getSprintsByUserId(userId: String): Flow<List<SprintEntity>> {
        TODO()
    }
    
    override fun getActiveSprints(currentDate: Date): Flow<List<SprintEntity>> {
        TODO()
    }
    
    override suspend fun getAllSprintsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        status: String?
    ): SprintListResponse {
        return try {
            val response = sprintApi.getAllSprints(page, limit, workspaceId, status)
            
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
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting sprint", e)
            false
        }
    }
    
    override suspend fun addItems(sprintId: String, tasks: List<String>?): SprintResponse {
        return try {
            val response = sprintApi.addItems(sprintId, AddItemsRequest(tasks))
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding items to sprint", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
    
    override suspend fun removeItems(sprintId: String, tasks: List<String>?): SprintResponse {
        return try {
            val response = sprintApi.removeItems(sprintId, RemoveItemsRequest(tasks))
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error removing items from sprint", e)
            // Return empty response with success=false when API fails
            SprintResponse(false, null)
        }
    }
    
    override suspend fun addItemsToSprint(sprintId: String, itemIds: List<String>): SprintResponse {
        return addItems(sprintId, itemIds)
    }
    
    override suspend fun removeItemsFromSprint(sprintId: String, itemIds: List<String>): SprintResponse {
        return removeItems(sprintId, itemIds)
    }
    
    override suspend fun getActiveSprintsFromApi(workspaceId: String): SprintListResponse {
        return try {
            val currentDate = Date()
            val response = sprintApi.getAllSprints(workspaceId = workspaceId, status = "In Progress")
            
            if (response.success) {
                // Lọc các sprints có thời gian hoạt động bao gồm ngày hiện tại
                val activeSprints = response.data.filter { sprint ->
                    sprint.start_date.before(currentDate) && sprint.end_date.after(currentDate) && sprint.status == "In Progress"
                }
                SprintListResponse(true, activeSprints.size, activeSprints.size, activeSprints)
            } else {
                response
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching active sprints from API", e)
            // Return empty response with success=false when API fails
            SprintListResponse(false, 0, 0, emptyList())
        }
    }
}