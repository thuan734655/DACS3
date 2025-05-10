package com.example.dacs3.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        WorkspaceEntity::class,
        ChannelEntity::class,
        MessageEntity::class,
        TaskEntity::class,
        BugEntity::class,
        EpicEntity::class,
        NotificationEntity::class,
        InvitationEntity::class,
        UserChannelMembership::class,
        WorkspaceUserMembership::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WorkspaceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun channelDao(): ChannelDao
    abstract fun messageDao(): MessageDao
    abstract fun taskDao(): TaskDao
    abstract fun bugDao(): BugDao
    abstract fun epicDao(): EpicDao
    abstract fun notificationDao(): NotificationDao
    abstract fun invitationDao(): InvitationDao
    abstract fun userChannelMembershipDao(): UserChannelMembershipDao
    abstract fun workspaceUserMembershipDao(): WorkspaceUserMembershipDao
    
    companion object {
        // Migration from version 2 to 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create a new tasks table with the required schema
                database.execSQL(
                    "CREATE TABLE tasks_new (" +
                    "taskId TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "createdBy TEXT NOT NULL, " +
                    "createdAt INTEGER NOT NULL, " +
                    "updatedAt INTEGER NOT NULL, " +
                    "priority INTEGER NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "progress INTEGER NOT NULL, " +
                    "assignedToUserId TEXT, " +
                    "epicId TEXT NOT NULL, " +
                    "PRIMARY KEY(taskId), " +
                    "FOREIGN KEY(createdBy) REFERENCES users(userId) ON DELETE CASCADE, " +
                    "FOREIGN KEY(epicId) REFERENCES epics(epicId) ON DELETE CASCADE)"
                )
                
                // Create default epic if none exists
                database.execSQL(
                    "INSERT OR IGNORE INTO epics (epicId, name, description, createdBy, priority, status, workspaceId) " +
                    "SELECT 'default-epic', 'Default Epic', 'Default Epic for migrated tasks', " +
                    "(SELECT userId FROM users LIMIT 1), 3, 'TO_DO', " +
                    "(SELECT workspaceId FROM workspaces LIMIT 1) " +
                    "WHERE NOT EXISTS (SELECT 1 FROM epics LIMIT 1)"
                )
                
                // Copy data from old table to new table
                database.execSQL(
                    "INSERT INTO tasks_new (taskId, name, description, createdBy, createdAt, " +
                    "updatedAt, priority, status, progress, assignedToUserId, epicId) " +
                    "SELECT taskId, name, description, createdBy, createdAt, " +
                    "updatedAt, priority, status, progress, assignedToUserId, " +
                    "IFNULL(epicId, (SELECT epicId FROM epics LIMIT 1)) FROM tasks"
                )
                
                // Remove the old table
                database.execSQL("DROP TABLE tasks")
                
                // Rename the new table to the correct name
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
                
                // Create indices
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_createdBy ON tasks(createdBy)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_epicId ON tasks(epicId)")
            }
        }
        
        @Volatile
        private var INSTANCE: WorkspaceDatabase? = null
        
        fun getDatabase(context: Context): WorkspaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkspaceDatabase::class.java,
                    "workspace_database"
                )
                    .fallbackToDestructiveMigration() // This will be used if migration fails
                    .addMigrations(MIGRATION_2_3)    // Try migration first
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 