package com.example.dacs3.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lớp tiện ích để lưu trữ và truy xuất thông tin workspace đã chọn
 */
@Singleton
class WorkspacePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREF_NAME = "workspace_preferences"
    private val KEY_SELECTED_WORKSPACE_ID = "selected_workspace_id"
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    /**
     * Lưu ID của workspace đã chọn
     */
    fun saveSelectedWorkspaceId(workspaceId: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_SELECTED_WORKSPACE_ID, workspaceId)
            apply()
        }
    }
    
    /**
     * Lấy ID của workspace đã chọn
     * @return String - ID của workspace đã chọn hoặc chuỗi rỗng nếu chưa chọn
     */
    fun getSelectedWorkspaceId(): String {
        return sharedPreferences.getString(KEY_SELECTED_WORKSPACE_ID, "") ?: ""
    }
    
    /**
     * Xóa ID của workspace đã chọn
     */
    fun clearSelectedWorkspaceId() {
        with(sharedPreferences.edit()) {
            remove(KEY_SELECTED_WORKSPACE_ID)
            apply()
        }
    }
}
