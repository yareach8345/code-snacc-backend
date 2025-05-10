package com.yareach.codesnaccbackend.dto.user

data class UserJoinDto(
    val id: String,
    val password: String,
    val nickname: String? = null,
    val icon: String? = null
)
