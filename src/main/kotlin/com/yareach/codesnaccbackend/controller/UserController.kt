package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.user.UserExistenceResponse
import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.dto.user.UserInfoEditDto
import com.yareach.codesnaccbackend.dto.user.UserJoinDto
import com.yareach.codesnaccbackend.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/user")
class UserController(
    val userService: UserService
) {
    @PostMapping()
    fun join(@RequestBody userJoinDto: UserJoinDto): ResponseEntity<Unit> {
        userService.join(userJoinDto)
        val location = URI.create("/user/${userJoinDto.userId}")
        return ResponseEntity.created(location).build()
    }

    @GetMapping("/me")
    fun getMyInfo(response: HttpServletResponse) {
        val myUserId = SecurityContextHolder.getContext().authentication.name
        response.sendRedirect("/user/$myUserId")
    }

    @PatchMapping("/me")
    fun updateUserInfo(@RequestBody userInfoEditDto: UserInfoEditDto): ResponseEntity<UserInfoDto> {
        val userId = SecurityContextHolder.getContext().authentication.name
        val userInfoAfterUpdate = userService.editUserInfo(userId, userInfoEditDto)
        return ResponseEntity.ok(userInfoAfterUpdate)
    }

    @DeleteMapping("/quit")
    fun quit(request: HttpServletRequest, response: HttpServletResponse): String{
        val id = SecurityContextHolder.getContext().authentication.name
        userService.quit(id)
        SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().authentication)
        return "quit success"
    }

    @GetMapping("/{userId}")
    fun getUserInfo(@PathVariable userId: String): ResponseEntity<UserInfoDto?> {
        val userInfo = userService.getUserInfo(userId)
        return ResponseEntity.ok(userInfo)
    }

    @GetMapping("/{userId}/check-id")
    fun isIdExists(@PathVariable userId: String): ResponseEntity<UserExistenceResponse> {
        val result = userService.isIdExists(userId)
        return ResponseEntity.ok(UserExistenceResponse(result))
    }
}