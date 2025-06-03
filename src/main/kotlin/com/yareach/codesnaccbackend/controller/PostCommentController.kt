package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.dto.comment.PostCommentDto
import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.service.CommentService
import com.yareach.codesnaccbackend.util.getUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/posts")
class PostCommentController(
    val commentService: CommentService,
) {
    @GetMapping("/{postId}/comments")
    fun getComments(@PathVariable postId: Int): ResponseEntity<List<CommentDto>> {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId))
    }

    @PostMapping("/{postId}/comments")
    fun postComment(@PathVariable postId: Int, @RequestBody postCommentDto: PostCommentDto): ResponseEntity<Unit> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication) ?: throw AccessDeniedException("Not LoggedIn")
        val commentId = commentService.postCommentByPostId(postId, userId, postCommentDto)
        return ResponseEntity.created(URI.create("/comments/${commentId}")).build()
    }
}