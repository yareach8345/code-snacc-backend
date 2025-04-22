package com.yareach.codesnaccbackend.dto.user

import com.yareach.codesnaccbackend.entity.UserEntity

data class UserInfoDto (
    val id: String,
    val nickName: String?,
    val role: String
)

fun UserEntity.toUserInfoDto() = UserInfoDto(id, nickName, role.name)