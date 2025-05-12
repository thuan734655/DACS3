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
    version = 6,
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
        // Migration from version 5 to 6
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add isDeviceVerified column to accounts table with default value of false
                database.execSQL("ALTER TABLE accounts ADD COLUMN isDeviceVerified INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // Migration from version 4 to 5
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // If you don't know the exact changes in version 5, you can create an empty migration
                // that just updates the version number
                // In a real production app, you should document each schema change
                database.execSQL("SELECT 1") // Dummy query to satisfy the migration interface
            }
        }
        
        // Migration from version 3 to 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // If you don't know the exact changes in version 4, you can create an empty migration
                // that just updates the version number
                // In a real production app, you should document each schema change
                database.execSQL("SELECT 1") // Dummy query to satisfy the migration interface
            }
        }
        
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
                // Instead of deleting database on each run, let's use proper migration
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkspaceDatabase::class.java,
                    "workspace_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6) // Add all migrations
                    .fallbackToDestructiveMigration() // Only use if migration fails
                    .allowMainThreadQueries() // Only for immediate init purposes
                    .build()
                
                // Set foreign key constraints
                instance.query("PRAGMA foreign_keys = ON", null)
                
                INSTANCE = instance
                instance
            }
        }
    }
} 