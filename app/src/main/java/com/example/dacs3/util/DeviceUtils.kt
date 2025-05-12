package com.example.dacs3.util

import android.content.Context
import android.provider.Settings

/**
 * Utility class for device identification related operations
 */
object DeviceUtils {

    /**
     * Gets a consistent device identifier using Android's Secure Settings
     * 
     * This method returns the ANDROID_ID which is a unique device identifier
     * that persists until a factory reset
     * 
     * @param context Android context needed to access content resolver
     * @return String representing the device's ANDROID_ID
     */
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown-device-id"
    }
} 