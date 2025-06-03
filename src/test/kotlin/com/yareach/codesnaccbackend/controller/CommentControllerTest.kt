package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.comment.PostCommentDto
import com.yareach.codesnaccbackend.repository.CommentRepository
import com.yareach.codesnaccbackend.service.CommentService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql",
    "classpath:db/scripts/init-comments.sql",
])
@Transactional
class CommentControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var commentService: CommentService

    @Autowired
    lateinit var commentRepository: CommentRepository

    fun prepareComment(): Int {
        val newCommentId = commentService.postCommentByPostId(
            postId = 0,
            userId = "test-user1",
            newCommentDto = PostCommentDto( content = "<CONTENT" )
        )

        val isUploaded = commentRepository.existsById(newCommentId)
        assertTrue(isUploaded)

        return newCommentId
    }

    fun checkIsDeleted(commentId: Int): Boolean {
        return !commentRepository.existsById(commentId)
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    @WithMockUser("test-user1")
    fun deleteComment() {
        val createdCommentsId = prepareComment()

        mockMvc.perform( delete("/comments/${createdCommentsId}") )
            .andExpect( status().isOk )

        val isDeleted = checkIsDeleted(createdCommentsId)
        assertTrue(isDeleted)
    }

    @Test
    @DisplayName("댓글 수정 테스트. 자신의 댓글이 아닐 경우 Forbidden을 반환하며 실패")
    @WithMockUser("test-user2")
    fun tryDeleteCommentButThisCommentIsNotMine() {
        val createdCommentsId = prepareComment()

        mockMvc.perform( delete("/comments/${createdCommentsId}") )
            .andExpect( status().isForbidden )

        val isDeleted = checkIsDeleted(createdCommentsId)
        assertFalse(isDeleted)
    }

    @Test
    @DisplayName("댓글 수정 테스트. 댓글이 없을 경우 NotFound 반환하며 실패")
    @WithMockUser("test-user2")
    fun tryDeleteCommentButThisCommentIsNotExists() {
        mockMvc.perform( delete("/comments/-1") )
            .andExpect( status().isNotFound )

    }

    @Test
    @DisplayName("댓글 id로 댓글 가져오기")
    fun getCommentByCommentId() {
        mockMvc.perform( get("/comments/1") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$.commentId").value(1) )
            .andExpect( jsonPath("$.writerId").value("test-user1") )
            .andExpect( jsonPath("$.content").value("<TEST COMMENT1>") )
    }

    @Test
    @DisplayName("댓글 id로 댓글 가져오기(댓글이 존재하지 않아 실패)")
    fun tryGetCommentByCommentIdButTheCommentIsNotExists() {
        mockMvc.perform( get("/comments/-1") )
            .andExpect( status().isNotFound )
    }
}