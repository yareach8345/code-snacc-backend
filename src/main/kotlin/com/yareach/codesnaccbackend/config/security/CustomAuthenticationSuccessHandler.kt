package com.yareach.codesnaccbackend.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.login.LoginSuccessDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class CustomAuthenticationSuccessHandler: AuthenticationSuccessHandler {
    val objectMapper = ObjectMapper()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val id = authentication.name
        val loginResult = LoginSuccessDto(
            id = id
        )

        val json = objectMapper.writeValueAsString(loginResult)

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(json)
    }
}