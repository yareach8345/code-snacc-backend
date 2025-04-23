package com.yareach.codesnaccbackend.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.login.LogoutResultDto

import com.yareach.codesnaccbackend.extensions.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

class CustomLogoutSuccessHandler: LogoutSuccessHandler {
    val objectMapper = ObjectMapper()
    val logger = logger()

    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        logger.info("logout: ${authentication.name}")
        val logoutResultDto = LogoutResultDto( id = authentication.name )
        val result = objectMapper.writeValueAsString(logoutResultDto)

        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json"
        response.writer?.write(result)
    }
}