package com.yareach.codesnaccbackend.exception

import com.yareach.codesnaccbackend.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserIdDuplicateException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateUserIdException(e: UserIdDuplicateException) = ErrorResponse(
        code = "USER_ID_DUPLICATE",
        message = e.message ?: "이미 사용 중인 아이디입니다: ${e.userId}"
    )

    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFoundException(e: UserNotFoundException) = ErrorResponse(
        code = "USER_NOT_FOUND",
        message = e.message ?: "해당 아이디의 유저를 찾을 수 없습니다: ${e.userId}"
    )
}