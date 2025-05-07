package com.yareach.codesnaccbackend.dto.user

data class UpdateField( val value: String? = null )

data class UserInfoUpdateDto(
    val password: UpdateField? = null,
    val nickName: UpdateField? = null,
    val icon: UpdateField? = null
)
