package com.example.dacs3.models

data class RegisterRequest(
    val username: String,
    val email: String,
    val contactNumber: String,
    val password: String
)

data class LoginRequest(
    val accountName: String,
    val password: String,
    val type: String, // "E" for email, "S" for phone
    val deviceID: String
)

data class LoginResponse(
    val success: Boolean?,
    val action: String?,
    val error: String?,
    val token: String?,
    val message: String,
    val account: Account? = null
)

data class RegisterResponse(
    val success: Boolean?,
    val action: String?,
    val error: String?,
    val message: String,
    val account: Account? = null
)

data class Account(
    val username: String,
    val email: String,
    val contactNumber: String
)

//data class RegisterRequest(
//    val username: String,
//    val email: String,
//    val contactNumber: String,
//    val password: String
//)
//data class RegisterResponse(val message: String)
//
//data class LoginRequest(
//    val accountName: String,
//    val password: String,
//    val type: String,
//    val deviceID: String? = null
//)
//data class LoginResponse(
//    val message: String,
//    val token: String,
//    val account: AccountInfo
//)
//data class AccountInfo(
//    val username: String,
//    val email: String,
//    val contactNumber: String
//)
