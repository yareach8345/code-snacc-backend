package com.yareach.codesnaccbackend.dto.user

data class UserJoinDto(
    val userId: String,
    val password: String,
    val nickName: String?
)
