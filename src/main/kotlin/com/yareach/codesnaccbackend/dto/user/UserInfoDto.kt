package com.yareach.codesnaccbackend.dto.user

data class UserInfoDto (
    val id: String,
    val nickName: String?,
    val role: String,
    val banned: Boolean,
    val quit: Boolean,
    val warnCnt: Byte,
    val icon: String?
)