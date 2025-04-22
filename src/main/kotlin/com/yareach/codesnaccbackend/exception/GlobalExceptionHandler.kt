package com.yareach.codesnaccbackend.exception

import com.yareach.codesnaccbackend.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.naming.AuthenticationException

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

    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(ex: AuthenticationException): ErrorResponse = ErrorResponse(
        code = "AUTHENTICATION_ERROR",
        message = "인증에 실패 했습니다. ${ex.message}"
    )

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ErrorResponse = ErrorResponse(
        code = "BAD_REQUEST",
        message = ex.message
    )
}