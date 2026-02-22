package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.dto.user.UserInfoUpdateDto
import com.yareach.codesnaccbackend.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me")
class MeController (
    val userService: UserService
) {
    @GetMapping
    fun getMyInfo(response: HttpServletResponse) {
        val myUserId = SecurityContextHolder.getContext().authentication.name
        response.sendRedirect("/users/$myUserId")
    }

    @PatchMapping
    fun updateUserInfo(@RequestBody userInfoUpdateDto: UserInfoUpdateDto): ResponseEntity<UserInfoDto> {
        val userId = SecurityContextHolder.getContext().authentication.name
        val userInfoAfterUpdate = userService.editUserInfo(userId, userInfoUpdateDto)
        return ResponseEntity.ok(userInfoAfterUpdate)
    }

    @PatchMapping("/quit")
    fun quit(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<Unit> {
        val id = SecurityContextHolder.getContext().authentication.name
        userService.quit(id)
        SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().authentication)
        return ResponseEntity.ok().build()
    }
}