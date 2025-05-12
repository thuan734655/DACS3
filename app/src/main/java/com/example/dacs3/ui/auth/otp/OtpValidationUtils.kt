package com.example.dacs3.ui.auth.otp

import android.content.Context
import android.provider.Settings
import android.util.Log

/**
 * Utility class for OTP validation logic
 */
object OtpValidationUtils {
    
    /**
     * Validates if the OTP is in proper format (6 digits)
     */
    fun isValidOtp(otp: String): Boolean {
        return otp.length == 6 && otp.all { it.isDigit() }
    }
    
    /**
     * Formats a string as an OTP by keeping only numeric characters and limiting to 6 digits
     */
    fun formatAsOtp(input: String): String {
        return input.filter { it.isDigit() }.take(6)
    }
    
    /**
     * Formats a phone number to a standardized format that OTP services often use
     * For example: +1 (234) 567-8901
     */
    fun formatPhoneNumber(phoneNumber: String): String {
        // This is a simple implementation. In a real app, you'd want a more sophisticated approach
        // that handles international formats
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        
        if (digitsOnly.length < 10) {
            return phoneNumber // Not enough digits to format
        }
        
        val lastTen = digitsOnly.takeLast(10)
        val countryCode = if (digitsOnly.length > 10) {
            "+${digitsOnly.dropLast(10)} "
        } else {
            ""
        }
        
        return "$countryCode(${lastTen.substring(0, 3)}) ${lastTen.substring(3, 6)}-${lastTen.substring(6)}"
    }
    
    /**
     * Gets a device identifier for OTP verification using Android's Settings.Secure.ANDROID_ID
     * 
     * @param context Android context needed to access device ID
     * @return String representing the device's unique identifier
     */
    fun getDeviceIdForOtp(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown-device-id"
        
        Log.d("OtpValidationUtils", "Direct Android ID for OTP: $androidId")
        
        // DeviceUtils is not a static utility class, so we can't access getDeviceId directly
        // If DeviceUtils is needed, it should be injected properly
        
        // Use direct Android ID to ensure consistency
        return androidId
    }
} 