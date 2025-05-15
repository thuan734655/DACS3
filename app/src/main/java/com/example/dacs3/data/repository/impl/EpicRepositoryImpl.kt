package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.EpicApi
import com.example.dacs3.data.local.dao.EpicDao
import com.example.dacs3.data.local.entity.EpicEntity
import com.example.dacs3.data.model.CreateEpicRequest
import com.example.dacs3.data.model.EpicListResponse
import com.example.dacs3.data.model.EpicResponse
import com.example.dacs3.data.model.UpdateEpicRequest
import com.example.dacs3.data.repository.EpicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpicRepositoryImpl @Inject constructor(
    private val epicDao: EpicDao,
    private val epicApi: EpicApi
) : EpicRepository {
    
    private val TAG = "EpicRepositoryImpl"
    
    override fun getAll(): Flow<List<EpicEntity>> {
        return epicDao.getAllEpics()
    }
    
    override suspend fun getById(id: String): EpicEntity? {
        return epicDao.getEpicById(id)
    }
    
    override suspend fun insert(item: EpicEntity) {
        epicDao.insertEpic(item)
    }
    
    override suspend fun insertAll(items: List<EpicEntity>) {
        epicDao.insertEpics(items)
    }
    
    override suspend fun update(item: EpicEntity) {
        epicDao.updateEpic(item)
    }
    
    override suspend fun delete(item: EpicEntity) {
        epicDao.deleteEpic(item)
    }
    
    override suspend fun deleteById(id: String) {
        epicDao.deleteEpicById(id)
    }
    
    override suspend fun deleteAll() {
        epicDao.deleteAllEpics()
    }
    
    override suspend fun sync() {
        try {
            val response = epicApi.getAllEpics()
            if (response.success && response.data != null) {
                val epics = response.data.map { EpicEntity.fromEpic(it) }
                epicDao.insertEpics(epics)
                Log.d(TAG, "Successfully synced ${epics.size} epics")
            } else {
                Log.w(TAG, "Failed to sync epics")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing epics", e)
        }
    }
    
    override fun getEpicsByWorkspaceId(workspaceId: String): Flow<List<EpicEntity>> {
        return epicDao.getEpicsByWorkspaceId(workspaceId)
    }
    
    override fun getEpicsByStatus(status: String): Flow<List<EpicEntity>> {
        return epicDao.getEpicsByStatus(status)
    }
    
    override fun getEpicsByAssignedTo(assignedTo: String): Flow<List<EpicEntity>> {
        return epicDao.getEpicsByAssignedTo(assignedTo)
    }
    
    override fun getEpicsBySprintId(sprintId: String): Flow<List<EpicEntity>> {
        return epicDao.getEpicsBySprintId(sprintId)
    }
    
    override suspend fun getAllEpicsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        status: String?,
        assignedTo: String?,
        sprintId: String?
    ): EpicListResponse {
        return try {
            val response = epicApi.getAllEpics(
                page, limit, workspaceId, status, assignedTo, sprintId
            )
            
            // If successful, store epics in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val epicEntities = response.data.map { EpicEntity.fromEpic(it) }
                    epicDao.insertEpics(epicEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching epics from API", e)
            // Return empty response with success=false when API fails
            EpicListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getEpicByIdFromApi(id: String): EpicResponse {
        return try {
            val response = epicApi.getEpicById(id)
            
            // If successful, store epic in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val epicEntity = EpicEntity.fromEpic(response.data)
                    epicDao.insertEpic(epicEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching epic from API", e)
            // Return empty response with success=false when API fails
            EpicResponse(false, null)
        }
    }
    
    override suspend fun createEpic(
        title: String,
        description: String?,
        workspaceId: String,
        assignedTo: String?,
        status: String?,
        priority: String?,
        startDate: Date?,
        dueDate: Date?,
        sprintId: String?
    ): EpicResponse {
        return try {
            val request = CreateEpicRequest(
                title, description, workspaceId, assignedTo, status, priority, 
                startDate, dueDate, sprintId
            )
            val response = epicApi.createEpic(request)
            
            // If successful, store epic in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val epicEntity = EpicEntity.fromEpic(response.data)
                    epicDao.insertEpic(epicEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating epic", e)
            // Return empty response with success=false when API fails
            EpicResponse(false, null)
        }
    }
    
    override suspend fun updateEpic(
        id: String,
        title: String?,
        description: String?,
        assignedTo: String?,
        status: String?,
        priority: String?,
        startDate: Date?,
        dueDate: Date?,
        completedDate: Date?,
        sprintId: String?
    ): EpicResponse {
        return try {
            val request = UpdateEpicRequest(
                title, description, assignedTo, status, priority, 
                startDate, dueDate, completedDate, sprintId
            )
            val response = epicApi.updateEpic(id, request)
            
            // If successful, update epic in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val epicEntity = EpicEntity.fromEpic(response.data)
                    epicDao.updateEpic(epicEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating epic", e)
            // Return empty response with success=false when API fails
            EpicResponse(false, null)
        }
    }
    
    override suspend fun deleteEpicFromApi(id: String): Boolean {
        return try {
            val response = epicApi.deleteEpic(id)
            
            // If successful, delete epic from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    epicDao.deleteEpicById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting epic", e)
            false
        }
    }
} 