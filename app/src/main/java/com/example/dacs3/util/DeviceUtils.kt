package com.example.dacs3.util

import android.app.Application
import android.provider.Settings
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for retrieving device identifiers and information
 */
@Singleton
class DeviceUtils @Inject constructor(
    private val application: Application
) {
    /**
     * Gets the device identifier using Android's Settings.Secure.ANDROID_ID
     * Used for authentication and OTP verification.
     * 
     * @return String representing the device's unique identifier
     */
    fun getDeviceId(): String {
        val deviceId = Settings.Secure.getString(
            application.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.d("DeviceUtils", "Retrieved device ID: $deviceId")
        return deviceId
    }
    
    /**
     * Gets information about the device, useful for debugging
     */
    fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "model" to android.os.Build.MODEL,
            "brand" to android.os.Build.BRAND,
            "manufacturer" to android.os.Build.MANUFACTURER,
            "sdk" to android.os.Build.VERSION.SDK_INT.toString(),
            "version" to android.os.Build.VERSION.RELEASE
        )
    }
} 