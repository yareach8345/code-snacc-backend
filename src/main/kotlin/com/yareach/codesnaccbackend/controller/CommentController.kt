package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.service.CommentService
import com.yareach.codesnaccbackend.util.getUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @GetMapping("/{commentId}")
    fun  getComment(@PathVariable("commentId") commentId: Int): ResponseEntity<CommentDto> {
        val result = commentService.getComment(commentId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{commentId}")
    fun deleteCommentById(@PathVariable("commentId") commentId: Int): ResponseEntity<Unit> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication) ?: throw AccessDeniedException("Not LoggedIn")
        commentService.deleteComment(commentId, userId)

        return ResponseEntity.ok().build()
    }
}