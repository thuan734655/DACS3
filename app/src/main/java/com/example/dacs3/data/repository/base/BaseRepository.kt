package com.example.dacs3.data.repository.base

import kotlinx.coroutines.flow.Flow

/**
 * Base repository interface that defines common methods for all repositories
 */
interface BaseRepository<T, ID> {
    /**
     * Get all entities as a Flow
     */
    fun getAll(): Flow<List<T>>
    
    /**
     * Get an entity by its ID
     */
    suspend fun getById(id: ID): T?
    
    /**
     * Insert an entity into the database
     */
    suspend fun insert(item: T)
    
    /**
     * Insert multiple entities into the database
     */
    suspend fun insertAll(items: List<T>)
    
    /**
     * Update an entity in the database
     */
    suspend fun update(item: T)
    
    /**
     * Delete an entity from the database
     */
    suspend fun delete(item: T)
    
    /**
     * Delete an entity by its ID
     */
    suspend fun deleteById(id: ID)
    
    /**
     * Delete all entities of this type
     */
    suspend fun deleteAll()
    
    /**
     * Sync data with remote server
     */
    suspend fun sync()
} 