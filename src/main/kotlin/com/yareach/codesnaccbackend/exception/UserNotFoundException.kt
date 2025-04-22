package com.yareach.codesnaccbackend.exception

class UserNotFoundException(
    val userId: String
): RuntimeException("해당 아이디의 유저는 존재하지 않습니다: $userId")