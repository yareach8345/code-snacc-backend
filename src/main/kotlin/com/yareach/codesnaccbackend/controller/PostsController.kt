package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.post.PostSearchOption
import com.yareach.codesnaccbackend.dto.post.PostSearchOptionType
import com.yareach.codesnaccbackend.dto.post.PostUploadDto
import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.exception.InvalidPageNumberException
import com.yareach.codesnaccbackend.exception.NotSupportSearchOptionException
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.SearchValueIsEmptyException
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
    @GetMapping
    fun getNPosts(
        @RequestParam searchBy: String?,
        @RequestParam searchValue: String?,
        @RequestParam page: Int?,
        @RequestParam pageSize: Int?
    ): ResponseEntity<List<PostInfoResponseDto>> {
        println("searchBy: $searchBy, searchValue: $searchValue, page: $page, pageSize: $pageSize")
        if (page != null && page < 1) {
           throw InvalidPageNumberException()
        }

        val searchOption = searchBy?.let { it -> PostSearchOption(
            PostSearchOptionType.entries.find { it.name == searchBy.uppercase() } ?: throw NotSupportSearchOptionException(searchBy),
            searchValue ?: throw SearchValueIsEmptyException(it)
        )}

        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        val result = postService.getNPosts(
            pageSize ?: 10,
            page?.let{ it - 1 } ?: 0,
            searchOption,
            userId,
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
    fun uploadPost(@RequestBody postUploadDto: PostUploadDto): ResponseEntity<Unit> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication)

        if(userId != postUploadDto.writerId) throw AccessDeniedException("작성자는 현재 로그인한 사용자여야 합니다.")

        val savedPostId = postService.uploadPost(postUploadDto)

        return ResponseEntity.created(URI.create("/posts/$savedPostId")).build()
    }
}