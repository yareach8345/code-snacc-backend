package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.repository.UserRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql"
])
@Transactional
class PostsControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var postRepository: UserRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("N개의 post 조회 테스트")
    fun getNPosts() {
        val pageSize = 3
        val pageNumber = 0

        val response = mockMvc.perform( get("/posts?pageSize=$pageSize&pageNumber=$pageNumber") )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
            .andExpect { jsonPath("$") }
            .andReturn()
            .response

        val postList = objectMapper.readValue(response.contentAsString, Array<Any>::class.java)

        assertEquals(pageSize, postList.size)
    }

    @Test
    @DisplayName("tag로 post 조회 테스트")
    fun getPostsByTag() {
        val tag = "test-tag1"
        val pageSize = 100
        val pageNumber = 0

        val response = mockMvc.perform( get("/posts?pageSize=$pageSize&pageNumber=$pageNumber&searchBy=tag&searchValue=$tag") )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isArray }
            .andExpect { jsonPath("$") }
            .andReturn()
            .response

        val postList = objectMapper.readValue(response.contentAsString, Array<PostInfoResponseDto>::class.java)

        println(postList.map { it.tags })
        println(postList.map { it.tags.contains(tag) })

        assertNotNull(postList)
        assertNotEquals(0, postList.size)
        assertTrue(postList.all { tag in it.tags })
    }

    @Test
    @DisplayName("게시글 id로 조회 테스트")
    fun getPostById() {
        val postId = 1

        val response = mockMvc.perform( get("/posts/$postId") )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isNotEmpty }
            .andExpect { jsonPath("$").isMap }
            .andExpect { jsonPath("$.id").value(postId) }
            .andReturn()
            .response

        val post = objectMapper.readValue(response.contentAsString, PostInfoResponseDto::class.java)

        assertNotNull(post)
        assertEquals(postId, post.id)
        assertEquals(false, post.didIRecommend)
    }

    @Test
    @DisplayName("내가 추천한 게시글은 didIRecommend 필드가 true 변경된다")
    @WithMockUser(username = "test-user1")
    fun getPostByIdNotFound() {
        val postId = 1

        val response = mockMvc.perform( get("/posts/$postId") )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isNotEmpty }
            .andExpect { jsonPath("$").isMap }
            .andExpect { jsonPath("$.id").value(postId) }
            .andReturn()
            .response

        val post = objectMapper.readValue(response.contentAsString, PostInfoResponseDto::class.java)
        assertEquals(true, post.didIRecommend)
    }

    @Test
    @DisplayName("랜덤으로 게시글 조회 테스트")
    fun getRandomPost() {
        mockMvc.perform( get("/posts/random") )
            .andExpect { status().isOk }
            .andExpect { jsonPath("$").isNotEmpty }
            .andExpect { jsonPath("$").isMap }
            .andReturn()
            .response
    }
}