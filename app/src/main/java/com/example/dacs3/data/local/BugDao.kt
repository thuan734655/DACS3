package com.example.dacs3.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BugDao {
    @Query("SELECT * FROM bugs")
    fun getAllBugs(): Flow<List<BugEntity>>
    
    @Query("SELECT * FROM bugs WHERE bugId = :bugId")
    suspend fun getBugById(bugId: String): BugEntity?
    
    @Query("SELECT * FROM bugs WHERE taskId = :taskId")
    fun getBugsByTaskId(taskId: String): Flow<List<BugEntity>>
    
    @Query("SELECT * FROM bugs WHERE taskId = :taskId")
    suspend fun getBugsByTaskIdSync(taskId: String): List<BugEntity>
    
    @Query("SELECT * FROM bugs WHERE createdBy = :userId")
    fun getBugsCreatedByUser(userId: String): Flow<List<BugEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBug(bug: BugEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBugs(bugs: List<BugEntity>)
    
    @Update
    suspend fun updateBug(bug: BugEntity)
    
    @Delete
    suspend fun deleteBug(bug: BugEntity)
    
    @Query("SELECT COUNT(*) FROM bugs")
    suspend fun getBugCount(): Int
} 