package com.yareach.codesnaccbackend.dto.login

data class LoginFailureDto(
    val success: Boolean = false,
    val message: String,
)
