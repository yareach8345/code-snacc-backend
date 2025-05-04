package com.yareach.codesnaccbackend.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.login.LoginSuccessDto
import com.yareach.codesnaccbackend.extensions.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class CustomAuthenticationSuccessHandler: AuthenticationSuccessHandler {
    val objectMapper = ObjectMapper()
    val logger = logger()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val id = authentication.name
        logger.info("login success : id")
        val loginResult = LoginSuccessDto(
            id = id
        )

        val json = objectMapper.writeValueAsString(loginResult)

        response.status = HttpServletResponse.SC_OK
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(json)
    }
}