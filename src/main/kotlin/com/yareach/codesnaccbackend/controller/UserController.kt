package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.dto.user.UserJoinDto
import com.yareach.codesnaccbackend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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

    @GetMapping("/{userId}")
    fun getUserInfo(@PathVariable userId: String): ResponseEntity<UserInfoDto?> {
        val userInfo = userService.getUserInfo(userId)
        return ResponseEntity.ok(userInfo)
    }
}