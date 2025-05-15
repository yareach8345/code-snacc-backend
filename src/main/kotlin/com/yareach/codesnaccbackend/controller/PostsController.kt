package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.exception.InvalidPageNumberException
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.service.PostService
import com.yareach.codesnaccbackend.util.getUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        if (page != null && page < 1) {
           throw InvalidPageNumberException()
        }

        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        val result = postService.getNPosts(
            pageSize ?: 10,
            page?.let{ it - 1 } ?: 0,
            userId
        )

        return ResponseEntity.ok(result)
    }

    @GetMapping("/random")
    fun getRandomPost(@RequestParam n: Int = 1): ResponseEntity<List<PostInfoResponseDto>> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        val result = postService.getNRandomPost(
            n = n,
            userId = userId
        )

        return if (result.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(result)
        }
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable postId: Int): ResponseEntity<PostInfoResponseDto> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        val result = postService.getPostById(postId, userId) ?: throw PostNotFoundException(postId)
        return ResponseEntity.ok(result)
    }
}