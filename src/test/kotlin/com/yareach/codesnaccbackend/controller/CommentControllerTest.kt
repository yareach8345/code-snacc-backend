package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.comment.CommentPostDto
import com.yareach.codesnaccbackend.dto.comment.CommentUpdateDto
import com.yareach.codesnaccbackend.repository.CommentRepository
import com.yareach.codesnaccbackend.service.CommentService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
            newCommentDto = CommentPostDto( content = "<CONTENT>" )
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

    @Test
    @DisplayName("댓글 수정")
    @WithMockUser("test-user1")
    fun updateComment() {
        val commentId = prepareComment()

        val commentUpdateDto = CommentUpdateDto(
            content = "<UPDATED_CONTENT>",
        )

        val commentUpdateBefore = commentRepository.findByIdOrNull(commentId)?.content

        mockMvc.perform(
            patch("/comments/${commentId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateDto)))
            .andExpect(status().isOk)

        val commentUpdateAfter = commentRepository.findByIdOrNull(commentId)?.content

        assertEquals("<CONTENT>", commentUpdateBefore)
        assertEquals("<UPDATED_CONTENT>", commentUpdateAfter)
    }

    @Test
    @DisplayName("댓글 수정하기(댓글이 존재하지 않아 실패)")
    @WithMockUser("test-user1")
    fun tryUpdateCommentButTheCommentIsNotExists() {
        val commentUpdateDto = CommentUpdateDto(
            content = "<UPDATED_CONTENT>",
        )
        mockMvc.perform(
            patch("/comments/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateDto)))
            .andExpect( status().isNotFound )
    }

    @Test
    @DisplayName("댓글 수정하기(댓글이 자신의 것이 아니라 실패)")
    @WithMockUser("test-user2")
    fun tryUpdateCommentButTheCommentIsNotMine() {
        val commentId = prepareComment()

        val commentUpdateDto = CommentUpdateDto(
            content = "<UPDATED_CONTENT>",
        )

        mockMvc.perform(
            patch("/comments/${commentId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentUpdateDto)))
            .andExpect( status().isForbidden )

        val commentUpdateAfter = commentRepository.findByIdOrNull(commentId)?.content
        assertNotEquals(commentUpdateDto.content, commentUpdateAfter)
    }
}