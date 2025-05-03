package com.yareach.codesnaccbackend.dto.login

data class AuthCheckResponseDto(
    val isAuth: Boolean,
    val username: String?
)
