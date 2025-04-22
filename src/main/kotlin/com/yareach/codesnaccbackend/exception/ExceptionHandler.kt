package com.yareach.codesnaccbackend.exception

import com.yareach.codesnaccbackend.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateUserIdException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateUserIdException(e: DuplicateUserIdException) = ErrorResponse(
        code = "USER_ID_DUPLICATE",
        message = e.message ?: "이미 사용 중인 아이디입니다: ${e.userId}"
    )
}