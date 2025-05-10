package com.yareach.codesnaccbackend.dto.user

data class UpdateField( val value: String? = null )

data class UserInfoUpdateDto(
    val password: UpdateField? = null,
    val nickname: UpdateField? = null,
    val icon: UpdateField? = null
)
