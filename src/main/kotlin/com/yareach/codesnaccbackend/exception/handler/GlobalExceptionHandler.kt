package com.yareach.codesnaccbackend.exception.handler

import com.yareach.codesnaccbackend.dto.ErrorResponse
import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.exception.InvalidPageNumberException
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.RequiredFieldIsNullException
import com.yareach.codesnaccbackend.exception.SearchValueIsEmptyException
import com.yareach.codesnaccbackend.exception.TagNotFoundException
import com.yareach.codesnaccbackend.exception.UserIdDuplicateException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import jakarta.transaction.NotSupportedException
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

    @ExceptionHandler(RequiredFieldIsNullException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleRequiredFieldIsNullException(ex: RequiredFieldIsNullException): ErrorResponse = ErrorResponse(
        code = "REQUIRED_FIELD_IS_NULL",
        message = ex.message
    )

    @ExceptionHandler(InvalidPageNumberException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidPageNumberException(ex: InvalidPageNumberException): ErrorResponse = ErrorResponse(
        code = "INVALID_PAGE_NUMBER",
        message = ex.message
    )

    @ExceptionHandler(PostNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handlePostNotFoundException(ex: PostNotFoundException): ErrorResponse = ErrorResponse(
        code = "POST_NOT_FOUND",
        message = ex.message
    )

    @ExceptionHandler(NotSupportedException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNotSupportedException(ex: NotSupportedException): ErrorResponse = ErrorResponse(
        code = "NOT_SUPPORTED_EXCEPTION",
        message = ex.message
    )

    @ExceptionHandler(SearchValueIsEmptyException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleSearchValueIsEmptyException(ex: SearchValueIsEmptyException): ErrorResponse = ErrorResponse(
        code = "SEARCH_VALUE_IS_EMPTY",
        message = ex.message
    )

    @ExceptionHandler(TagNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTagNotFoundException(ex: TagNotFoundException): ErrorResponse = ErrorResponse(
        code = "TAG_NOT_FOUND",
        message = ex.message
    )

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException): ErrorResponse = ErrorResponse(
        code = "ACCESS_DENIED",
        message = ex.message ?: "접근이 거부되었습니다."
    )
}