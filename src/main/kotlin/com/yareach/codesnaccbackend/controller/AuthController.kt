package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.login.AuthCheckResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/auth")
class AuthController {
    @RequestMapping("/check")
    fun login(): ResponseEntity<AuthCheckResponseDto> {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val isAuth = authentication.isAuthenticated && authentication !is AnonymousAuthenticationToken
        return ResponseEntity.ok(AuthCheckResponseDto(
            isAuth = isAuth,
            username= SecurityContextHolder.getContext().authentication.name
        ))
    }
}