package com.yareach.codesnaccbackend.exception

class AccessDeniedException (override val message: String?) : RuntimeException(
    message ?: "Access denied"
)