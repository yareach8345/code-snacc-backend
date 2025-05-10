package com.yareach.codesnaccbackend.dto.user

data class UserInfoDto (
    val id: String,
    val nickname: String?,
    val role: String,
    val banned: Boolean,
    val quit: Boolean,
    val warnCnt: Byte,
    val icon: String?
)