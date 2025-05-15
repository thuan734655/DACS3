package com.example.dacs3.data.repository.impl

import android.util.Log
import com.example.dacs3.data.api.BugApi
import com.example.dacs3.data.local.dao.BugDao
import com.example.dacs3.data.local.entity.BugEntity
import com.example.dacs3.data.model.AddCommentRequest
import com.example.dacs3.data.model.BugListResponse
import com.example.dacs3.data.model.BugResponse
import com.example.dacs3.data.model.CommentResponse
import com.example.dacs3.data.model.CreateBugRequest
import com.example.dacs3.data.model.UpdateBugRequest
import com.example.dacs3.data.repository.BugRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BugRepositoryImpl @Inject constructor(
    private val bugDao: BugDao,
    private val bugApi: BugApi
) : BugRepository {
    
    private val TAG = "BugRepositoryImpl"
    
    override fun getAll(): Flow<List<BugEntity>> {
        return bugDao.getAllBugs()
    }
    
    override suspend fun getById(id: String): BugEntity? {
        return bugDao.getBugById(id)
    }
    
    override suspend fun insert(item: BugEntity) {
        bugDao.insertBug(item)
    }
    
    override suspend fun insertAll(items: List<BugEntity>) {
        bugDao.insertBugs(items)
    }
    
    override suspend fun update(item: BugEntity) {
        bugDao.updateBug(item)
    }
    
    override suspend fun delete(item: BugEntity) {
        bugDao.deleteBug(item)
    }
    
    override suspend fun deleteById(id: String) {
        bugDao.deleteBugById(id)
    }
    
    override suspend fun deleteAll() {
        bugDao.deleteAllBugs()
    }
    
    override suspend fun sync() {
        try {
            val response = bugApi.getAllBugs()
            if (response.success && response.data != null) {
                val bugs = response.data.map { BugEntity.fromBug(it) }
                bugDao.insertBugs(bugs)
                Log.d(TAG, "Successfully synced ${bugs.size} bugs")
            } else {
                Log.w(TAG, "Failed to sync bugs")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing bugs", e)
        }
    }
    
    override fun getBugsByWorkspaceId(workspaceId: String): Flow<List<BugEntity>> {
        return bugDao.getBugsByWorkspaceId(workspaceId)
    }
    
    override fun getBugsByTaskId(taskId: String): Flow<List<BugEntity>> {
        return bugDao.getBugsByTaskId(taskId)
    }
    
    override fun getBugsByReportedBy(reportedBy: String): Flow<List<BugEntity>> {
        return bugDao.getBugsByReportedBy(reportedBy)
    }
    
    override fun getBugsByAssignedTo(assignedTo: String): Flow<List<BugEntity>> {
        return bugDao.getBugsByAssignedTo(assignedTo)
    }
    
    override fun getBugsByStatus(status: String): Flow<List<BugEntity>> {
        return bugDao.getBugsByStatus(status)
    }
    
    override suspend fun getAllBugsFromApi(
        page: Int?,
        limit: Int?,
        workspaceId: String?,
        taskId: String?,
        reportedBy: String?,
        assignedTo: String?,
        status: String?,
        severity: String?
    ): BugListResponse {
        return try {
            val response = bugApi.getAllBugs(
                page, limit, workspaceId, taskId, reportedBy, assignedTo, status, severity
            )
            
            // If successful, store bugs in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val bugEntities = response.data.map { BugEntity.fromBug(it) }
                    bugDao.insertBugs(bugEntities)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching bugs from API", e)
            // Return empty response with success=false when API fails
            BugListResponse(false, 0, 0, emptyList())
        }
    }
    
    override suspend fun getBugByIdFromApi(id: String): BugResponse {
        return try {
            val response = bugApi.getBugById(id)
            
            // If successful, store bug in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val bugEntity = BugEntity.fromBug(response.data)
                    bugDao.insertBug(bugEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching bug from API", e)
            // Return empty response with success=false when API fails
            BugResponse(false, null)
        }
    }
    
    override suspend fun createBug(
        title: String,
        description: String?,
        workspaceId: String,
        taskId: String?,
        assignedTo: String?,
        status: String?,
        severity: String?,
        stepsToReproduce: String?,
        expectedBehavior: String?,
        actualBehavior: String?
    ): BugResponse {
        return try {
            val request = CreateBugRequest(
                title, description, workspaceId, taskId, assignedTo, status, severity,
                stepsToReproduce, expectedBehavior, actualBehavior
            )
            val response = bugApi.createBug(request)
            
            // If successful, store bug in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val bugEntity = BugEntity.fromBug(response.data)
                    bugDao.insertBug(bugEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error creating bug", e)
            // Return empty response with success=false when API fails
            BugResponse(false, null)
        }
    }
    
    override suspend fun updateBug(
        id: String,
        title: String?,
        description: String?,
        taskId: String?,
        assignedTo: String?,
        status: String?,
        severity: String?,
        stepsToReproduce: String?,
        expectedBehavior: String?,
        actualBehavior: String?
    ): BugResponse {
        return try {
            val request = UpdateBugRequest(
                title, description, taskId, assignedTo, status, severity,
                stepsToReproduce, expectedBehavior, actualBehavior
            )
            val response = bugApi.updateBug(id, request)
            
            // If successful, update bug in local database
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    val bugEntity = BugEntity.fromBug(response.data)
                    bugDao.updateBug(bugEntity)
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error updating bug", e)
            // Return empty response with success=false when API fails
            BugResponse(false, null)
        }
    }
    
    override suspend fun deleteBugFromApi(id: String): Boolean {
        return try {
            val response = bugApi.deleteBug(id)
            
            // If successful, delete bug from local database
            if (response.success) {
                withContext(Dispatchers.IO) {
                    bugDao.deleteBugById(id)
                }
            }
            
            response.success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting bug", e)
            false
        }
    }
    
    override suspend fun addComment(bugId: String, content: String): CommentResponse {
        return try {
            val request = AddCommentRequest(content)
            val response = bugApi.addComment(bugId, request)
            
            // If successful, update bug in local database with the new comment
            if (response.success && response.data != null) {
                withContext(Dispatchers.IO) {
                    // Get the current bug
                    val bug = bugDao.getBugById(bugId)
                    
                    // If the bug exists locally, update it
                    if (bug != null) {
                        val updatedBug = getBugByIdFromApi(bugId)
                        if (updatedBug.success && updatedBug.data != null) {
                            val bugEntity = BugEntity.fromBug(updatedBug.data)
                            bugDao.updateBug(bugEntity)
                        }
                    }
                }
            }
            
            response
        } catch (e: Exception) {
            Log.e(TAG, "Error adding comment to bug", e)
            // Return empty response with success=false when API fails
            CommentResponse(false, null)
        }
    }
} 