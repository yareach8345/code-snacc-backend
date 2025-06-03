package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.comment.CommentDto
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql",
    "classpath:db/scripts/init-comments.sql",
])
@Transactional
class PostCommentControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("게시글의 댓글 얻어오기")
    fun getPostsComments() {
        val response = mockMvc.perform( get("/posts/0/comments") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$").isArray )
            .andReturn()
            .response
            .let{ objectMapper.readValue(it.contentAsString, Array<CommentDto>::class.java) }
            .toList()

        assertEquals(3, response.size)
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 게시글 불러오면 404 에러코드 반환")
    fun tryGetCommentsFromNotExistsPost() {
        mockMvc.perform( get("/posts/100/comments") )
            .andExpect( status().isNotFound )
            .andReturn()
    }
}