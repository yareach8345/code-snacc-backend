package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.recommend.RecommendDto
import com.yareach.codesnaccbackend.dto.recommend.UpdateRecommendResponse
import jakarta.transaction.Transactional
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql",
    "classpath:db/scripts/init-comments.sql",
])
@Transactional
class RecommendControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("recommend 작업")
    @WithMockUser(username = "test-user1")
    fun recommendTest() {
        val idOfAPostNotRecommendedByTestUser1 = 2

        val recommendBeforeRecommend = mockMvc.perform(get("/posts/${idOfAPostNotRecommendedByTestUser1}/recommend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, RecommendDto::class.java)}
            .recommendCnt

        val recommendAfterRecommend = mockMvc.perform(post("/posts/${idOfAPostNotRecommendedByTestUser1}/recommend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.didIRecommended").value(true))
            .andExpect(jsonPath("$.recommendCnt").isNumber)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, UpdateRecommendResponse::class.java)}
            .recommendCnt

        val diffRecommendCnt = recommendAfterRecommend - recommendBeforeRecommend
        assertEquals(1, diffRecommendCnt)
    }

    @Test
    @DisplayName("cancel recommend 작업")
    @WithMockUser(username = "test-user1")
    fun cancelRecommendTest() {
        val idOfAPostNotRecommendedByTestUser1 = 0

        val recommendBeforeRecommend = mockMvc.perform(get("/posts/${idOfAPostNotRecommendedByTestUser1}/recommend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, RecommendDto::class.java)}
            .recommendCnt

        val recommendAfterRecommend = mockMvc.perform(delete("/posts/${idOfAPostNotRecommendedByTestUser1}/recommend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.didIRecommended").value(false))
            .andExpect(jsonPath("$.recommendCnt").isNumber)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, UpdateRecommendResponse::class.java)}
            .recommendCnt

        val diffRecommendCnt = recommendAfterRecommend - recommendBeforeRecommend
        assertEquals(-1, diffRecommendCnt)
    }

    @Test
    @DisplayName("로그인 없이 recommend 작업")
    fun recommendTestWithoutLogin() {
        mockMvc.perform(post("/posts/0/recommend"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("로그인 없이 cancel recommend 작업")
    fun cancelRecommendTestWithoutLogin() {
        mockMvc.perform(delete("/posts/0/recommend"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("없는 게시글에 recommend 작업")
    @WithMockUser(username = "test-user1")
    fun recommendAPostNotExistsTest() {
        mockMvc.perform(post("/posts/999/recommend"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("없는 게시글에 cancel recommend 작업")
    @WithMockUser(username = "test-user1")
    fun cancelRecommendAPostNotExistsTestWithoutLogin() {
        mockMvc.perform(delete("/posts/999/recommend"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("특정 개시글의 recommend 수 가져오기")
    fun getRecommendCnt() {
        val recommendCntExpect = mockMvc.perform(get("/posts/0"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, PostInfoResponseDto::class.java)}
            .recommendCnt

        val recommendCnt = mockMvc.perform(get("/posts/0/recommend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.recommendCnt").isNumber)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, RecommendDto::class.java)}
            .recommendCnt

        assertEquals(recommendCntExpect.toLong(), recommendCnt)
    }

    @Test
    @DisplayName("존재하지 않는 게시글의 recommend 수를 가져오면 0")
    fun getRecommendCntOfUnExistsPost() {
        val recommendCnt = mockMvc.perform(get("/posts/99/recommend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isNotEmpty)
            .andExpect(jsonPath("$.recommendCnt").isNumber)
            .andReturn()
            .response.contentAsString
            .let{objectMapper.readValue(it, RecommendDto::class.java)}
            .recommendCnt

        assertEquals(0, recommendCnt)
    }
}