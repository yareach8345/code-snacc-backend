package com.yareach.codesnaccbackend.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.post.PostUpdateDto
import com.yareach.codesnaccbackend.dto.post.PostUploadDto
import com.yareach.codesnaccbackend.dto.post.PostUploadResponseDto
import com.yareach.codesnaccbackend.dto.post.SearchPostResultDto
import jakarta.transaction.Transactional
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import kotlin.test.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.jvm.java

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = [
    "classpath:db/scripts/init-users.sql",
    "classpath:db/scripts/init-posts.sql",
    "classpath:db/scripts/init-comments.sql",
])
@Transactional
class PostControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("N개의 post 조회 테스트")
    fun getNPosts() {
        val pageSize = 3
        val pageNumber = 1

        val response = mockMvc.perform( get("/posts?pageSize=$pageSize&page=$pageNumber") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$.posts").isArray )
            .andReturn()
            .response

        val searchPostResult = objectMapper.readValue(response.contentAsString, SearchPostResultDto::class.java)

        assertEquals(pageSize, searchPostResult.posts.size)
    }

    @Test
    @DisplayName("tag로 post 조회 테스트")
    fun getPostsByTag() {
        val tag = "test-tag1"
        val pageSize = 100
        val pageNumber = 0

        val response = mockMvc.perform( get("/posts?pageSize=$pageSize&pageNumber=$pageNumber&tags=$tag") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$.posts").isArray )
            .andReturn()
            .response

        val result = objectMapper.readValue(response.contentAsString, SearchPostResultDto::class.java)
        val postList = result.posts

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

        mockMvc.perform( get("/posts/$postId") )
            .andExpect( status().isOk)
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$").isMap )
            .andExpect( jsonPath("$.id").value(postId) )
            .andReturn()
            .response
    }

    @Test
    @DisplayName("내가 추천한 게시글은 didIRecommend 필드가 true 변경된다")
    @WithMockUser(username = "test-user1")
    fun getPostByIdNotFound() {
        val postId1 = 0

        mockMvc.perform( get("/posts/${postId1}") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$").isMap )
            .andExpect( jsonPath("$.id").value(postId1) )
            .andExpect( jsonPath("$.didIRecommend").value(true) )
            .andReturn()
            .response

        val postId2 = 1

        mockMvc.perform( get("/posts/$postId2") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$").isMap )
            .andExpect( jsonPath("$.id").value(postId2) )
            .andExpect( jsonPath("$.didIRecommend").value(false) )
            .andReturn()
            .response
    }

    @Test
    @DisplayName("랜덤으로 게시글 조회 테스트")
    fun getRandomPost() {
        mockMvc.perform( get("/posts/random") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$").isArray )
            .andReturn()
            .response
            .let { objectMapper.readValue(it.contentAsString, Array<PostInfoResponseDto>::class.java) }
            .toList()
    }

    @Test
    @DisplayName("게시글로 댓글 조회")
    fun getCommentCnt() {
        mockMvc.perform( get("/posts/0") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$.commentCnt").value(3) )
            .andReturn()
            .response
    }

    @Test
    @DisplayName("게시글 작성 테스트")
    @WithMockUser(username = "test-user1")
    fun createPost() {
        val postDto = PostUploadDto(
            title = "test-title",
            code = "test-code",
            language = "test-language",
            content = "test-content",
            tags = listOf("test-tag1", "test-tag2")
        )

        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", Matchers.startsWith("/posts/")))
            .andReturn()
            .response
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @WithMockUser(username = "test-user1")
    fun deletePost() {
        val postDto = PostUploadDto(
            title = "delete-test",
            code = "test-code",
            language = "test-language",
            content = "test-content",
            tags = listOf("test-tag1", "test-tag2")
        )

        val postResponse = mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
            .andExpect( status().isCreated )
            .andReturn()
            .response

        val postUploadResult = objectMapper.readValue(postResponse.contentAsString, PostUploadResponseDto::class.java)

        mockMvc.perform( get("/posts/${postUploadResult.postId}") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$.id").value(postUploadResult.postId) )
            .andReturn()
            .response

        mockMvc.perform( delete("/posts/${postUploadResult.postId}") )
            .andExpect( status().isOk )
            .andReturn()
            .response

        mockMvc.perform( get("/posts/${postUploadResult.postId}") )
            .andExpect( status().isNotFound )
            .andReturn()
            .response
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @WithMockUser(username = "test-user1")
    fun deletePostWithWrongId() {
        mockMvc.perform( get("/posts/2") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$.writerId").value("test-user2") )
            .andReturn()
            .response
            .contentAsString
            .let { println(it) }

        mockMvc.perform( delete("/posts/2") )
            .andExpect( status().isForbidden )
            .andReturn()
            .response
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @WithMockUser(username = "test-user1")
    fun updatePost() {
        val postDto = PostUploadDto(
            title = "delete-test",
            code = "test-code",
            language = "test-language",
            content = "test-content",
            tags = listOf("test-tag1", "test-tag2")
        )

        val postResponse = mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
            .andExpect( status().isCreated )
            .andReturn()
            .response

        val postUploadResult = objectMapper.readValue(postResponse.contentAsString, PostUploadResponseDto::class.java)

        mockMvc.perform( get("/posts/${postUploadResult.postId}") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$.id").value(postUploadResult.postId) )
            .andReturn()
            .response

        val updateDto = PostUpdateDto(
            title = "update-title",
            code = "update-code",
            language = "update-language",
            content = "update-content",
            tags = listOf("test-tag3")
        )

        mockMvc.perform(
            patch("/posts/${postUploadResult.postId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect( status().isOk )
            .andReturn()
            .response

        val responseAfterUpdate = mockMvc.perform( get("/posts/${postUploadResult.postId}") )
            .andExpect( status().isOk )
            .andExpect( jsonPath("$").isNotEmpty )
            .andExpect( jsonPath("$.title").value("update-title") )
            .andExpect( jsonPath("$.code").value("update-code") )
            .andExpect( jsonPath("$.language").value("update-language") )
            .andExpect( jsonPath("$.content").value("update-content") )
            .andReturn()
            .response

        val postInfoAfterUpdate = objectMapper.readValue(responseAfterUpdate.contentAsString, PostInfoResponseDto::class.java)

        assertEquals(postInfoAfterUpdate.tags.toSet(), setOf("test-tag3"))
    }
}