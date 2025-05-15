package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.exception.InvalidPageNumberException
import com.yareach.codesnaccbackend.service.PostService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostsController(
    private val postService: PostService
) {
    @GetMapping
    fun getNPosts(@RequestParam page: Int?, @RequestParam pageSize: Int?): ResponseEntity<List<PostInfoResponseDto>> {
        val userAuthentication = SecurityContextHolder.getContext().authentication
        if (page != null && page < 1) {
           throw InvalidPageNumberException()
        }

        val userId = when(userAuthentication) {
            is AnonymousAuthenticationToken -> null
            else -> userAuthentication?.name
        }

        val result = postService.getNPosts(
            pageSize ?: 10,
            page?.let{ it - 1 } ?: 0,
            userId
        )

        return ResponseEntity.ok(result)
    }
}