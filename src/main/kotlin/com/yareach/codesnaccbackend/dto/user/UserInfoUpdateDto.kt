package com.yareach.codesnaccbackend.dto.user

data class UserInfoUpdateDto(
    val password: String? = null,
    val nickname: String? = null,
    val icon: String? = null
)
