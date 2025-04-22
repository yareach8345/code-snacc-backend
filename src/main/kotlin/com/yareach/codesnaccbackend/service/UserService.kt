package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.dto.user.UserJoinDto

interface UserService {
    fun join(userJoinDto: UserJoinDto)

    fun getUserInfo(id: String): UserInfoDto

    fun quit(id: String)

    fun isIdExists(id: String): Boolean
}