package com.yareach.codesnaccbackend.exception


class DuplicateUserIdException(
    val userId: String
): RuntimeException("이미 사용중인 아이디입니다: $userId")
