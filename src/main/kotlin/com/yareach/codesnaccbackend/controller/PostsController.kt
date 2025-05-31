package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.post.PostSearchDto
import com.yareach.codesnaccbackend.dto.post.SearchPostResultDto
import com.yareach.codesnaccbackend.dto.post.PostUploadDto
import com.yareach.codesnaccbackend.dto.post.PostUploadResponseDto
import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.exception.InvalidPageNumberException
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.extensions.logger
import com.yareach.codesnaccbackend.service.PostService
import com.yareach.codesnaccbackend.util.getUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/posts")
class PostsController(
    private val postService: PostService
) {
    val logger = logger()

    @GetMapping
    fun getNPosts(
        @RequestParam title: String?,
        @RequestParam writerId: String?,
        @RequestParam tags: List<String>?,
        @RequestParam lang: String?,
        @RequestParam page: Int?,
        @RequestParam pageSize: Int?
    ): ResponseEntity<SearchPostResultDto> {
        if (page != null && page < 1) {
           throw InvalidPageNumberException()
        }

        val searchDto = PostSearchDto(title, writerId, tags, lang)

        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        val result = postService.searchPosts(
            pageSize ?: 10,
            page?.let{ it - 1 } ?: 0,
            userId,
            searchDto
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

    @PostMapping
    fun uploadPost(@RequestBody postUploadDto: PostUploadDto): ResponseEntity<PostUploadResponseDto> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        if(userId != postUploadDto.writerId) throw AccessDeniedException("작성자는 현재 로그인한 사용자여야 합니다.")

        val savedPostId = postService.uploadPost(postUploadDto)
        logger.info("post가 업로드 되었습니다. ${postUploadDto.title}($savedPostId) by ${postUploadDto.writerId}")

        return ResponseEntity.created(URI.create("/posts/$savedPostId")).body(PostUploadResponseDto(savedPostId))
    }
}