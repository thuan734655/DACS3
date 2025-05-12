package com.example.dacs3.util

import android.util.Log
import android.util.Patterns

object ValidationUtils {
    private val USERNAME_REGEX = Regex("^[a-zA-Z]{3,50}$")
    private val PHONE_REGEX = Regex("^\\d{10}$")
    private val PASSWORD_REGEX = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}")

    fun validateUsername(username: String): ValidationResult =
        if (username.isEmpty()) ValidationResult.Error("Username is required")
        else if (!USERNAME_REGEX.matches(username)) ValidationResult.Error("3-10 letters only")
        else ValidationResult.Success

    fun validateEmail(email: String): ValidationResult =
        if (email.isEmpty()) ValidationResult.Error("Email is required")
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) ValidationResult.Error("Invalid email format")
        else ValidationResult.Success

    fun validatePhone(phone: String): ValidationResult =
        if (phone.isEmpty()) ValidationResult.Error("Contact number is required")
        else if (!PHONE_REGEX.matches(phone)) ValidationResult.Error("Invalid phone number")
        else ValidationResult.Success

    fun validatePassword(password: String): ValidationResult =
        if (password.isEmpty()) ValidationResult.Error("Password is required")
        else if (!PASSWORD_REGEX.matches(password)) ValidationResult.Error("Password must be at least 8 characters, include upper, lower, number, special char")
        else ValidationResult.Success
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
} 