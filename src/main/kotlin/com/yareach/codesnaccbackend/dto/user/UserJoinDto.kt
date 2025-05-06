package com.yareach.codesnaccbackend.dto.user

data class UserJoinDto(
    val id: String,
    val password: String,
    val nickName: String? = null,
    val icon: String? = null
)
