package com.yareach.codesnaccbackend.controller

import com.yareach.codesnaccbackend.exception.AccessDeniedException
import com.yareach.codesnaccbackend.service.CommentService
import com.yareach.codesnaccbackend.util.getUserId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService,
) {
    @DeleteMapping("/{id}")
    fun deleteCommentById(@PathVariable("id") id: Int): ResponseEntity<Unit> {
        val userId = getUserId(SecurityContextHolder.getContext().authentication) ?: throw AccessDeniedException("Not LoggedIn")
        commentService.deleteComment(id, userId)

        return ResponseEntity.ok().build()
    }
}