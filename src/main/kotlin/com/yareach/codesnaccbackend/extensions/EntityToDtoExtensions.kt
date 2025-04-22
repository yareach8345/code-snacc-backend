package com.yareach.codesnaccbackend.extensions

import com.yareach.codesnaccbackend.config.security.CustomUserDetails
import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.entity.UserEntity

fun UserEntity.toUserInfoDto() = UserInfoDto(id, nickName, role.name, banned, quit, warnCnt)

fun UserEntity.toCustomUserDetails() = CustomUserDetails(this)