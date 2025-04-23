package com.yareach.codesnaccbackend.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.login.LoginFailureDto
import com.yareach.codesnaccbackend.extensions.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class CustomAuthenticationFailureHandler: AuthenticationFailureHandler {
    val objectMapper = ObjectMapper()
    val logger = logger()

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        logger.info("login failed : ${exception.message}")
        val loginResult = LoginFailureDto(
            message = "로그인에 실패하였습니다.",
        )

        val json = objectMapper.writeValueAsString(loginResult)

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = Charsets.UTF_8.name()
        response.writer.write(json)
    }
}