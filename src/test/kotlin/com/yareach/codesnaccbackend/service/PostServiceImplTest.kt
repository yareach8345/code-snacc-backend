package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.TagEntity
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.entity.UserRole
import com.yareach.codesnaccbackend.exception.RequiredFieldIsNullException
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.repository.PostRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

class PostServiceImplTest {
    val postRepository = mockk<PostRepository>()

    val postService = PostServiceImpl(postRepository)

    @Test
    @DisplayName("N개의 post 조회 테스트")
    fun getNPosts() {
        val capturePost = slot<Pageable>()
        every { postRepository.findAllByDeletedIsFalseOrderByWrittenAtDesc(capture(capturePost)) }
            .answers {
                val startId = capturePost.captured.pageNumber * capturePost.captured.pageSize
                val endId = startId + capturePost.captured.pageSize

                val mockUserEntity = UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                )

                (startId..< endId)
                    .map {
                        PostEntity(
                        id = it,
                        writer = mockUserEntity,
                        title = "test post $it",
                        code = "<hello code $it>",
                        language = "<lang>",
                        content = "<content>",
                        writtenAt = LocalDateTime.now(),
                        tags = mutableSetOf(TagEntity(tag = "test-tag")),
                        recommends = mutableSetOf(mockUserEntity)
                    )}
            }

        // 10개를 한 페이지의 크기로. 첫번째 페이지를 가져옴
        val postsOnFirstPage = postService.getNPosts(10, 0, null)

        assertEquals(0, capturePost.captured.pageNumber)
        assertEquals(10, capturePost.captured.pageSize)

        assertEquals(10, postsOnFirstPage.size)
        assertEquals(0, postsOnFirstPage.first().id)
        assertEquals(9, postsOnFirstPage.last().id)

        // 5개를 한 페이지의 크기로. 세번째 페이지를 가져옴
        val postsOnThirdPage = postService.getNPosts(5, 2, null)

        assertEquals(2, capturePost.captured.pageNumber)
        assertEquals(5, capturePost.captured.pageSize)

        assertEquals(5, postsOnThirdPage.size)
        assertEquals(10, postsOnThirdPage.first().id)
        assertEquals(14, postsOnThirdPage.last().id)
    }

    @Test
    @DisplayName("id로 post 조회 테스트")
    fun getPostById() {
        val capturePost = slot<Int>()
        every { postRepository.findOrThrow(capture(capturePost)){ RuntimeException("post not found")} }
            .answers { PostEntity(
                id = capturePost.captured,
                writer = UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ),
                title = "test post",
                code = "<hello code>",
                language = "<lang>",
                content = "<content>",
                writtenAt = LocalDateTime.now(),
                tags = mutableSetOf(TagEntity(tag = "test-tag")),
                recommends = mutableSetOf(UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ))
            ) }

        val post = postService.getPostById(1)

        assertEquals(1, capturePost.captured)
        assertNotNull(post)
        assertEquals(1, post?.id)
    }

    @Test
    @DisplayName("내가 추천한 post 조회 테스트")
    fun getMyRecommendedPosts() {
        every { postRepository.getRandomPost() }
            .answers { PostEntity(
                id = 1,
                writer = UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ),
                title = "test post",
                code = "<hello code>",
                language = "<lang>",
                content = "<content>",
                writtenAt = LocalDateTime.now(),
                tags = mutableSetOf(TagEntity(tag = "test-tag")),
                recommends = mutableSetOf(UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                )))}

        //익명으로 조회
        val post1 = postService.getRandomPost()
        assertNotNull(post1)
        assertEquals(false, post1?.didIRecommend)

        //추천하지 않은 유저의 닉네임으로 조회
        val post2 = postService.getRandomPost(userId =  "user did not recommend this post")
        assertNotNull(post2)
        assertEquals(false, post2?.didIRecommend)

        //추천한 유저의 닉네임으로 조회
        val post3 = postService.getRandomPost(userId = "testuser")
        assertNotNull(post3)
        assertEquals(true, post3?.didIRecommend)
    }

    @Test
    @DisplayName("repository로 받은 post에 null이 있음 (실제로는 발생할 확률 거의 없음)")
    fun getPostByIdWithNull() {
        val capturePost = slot<Int>()
        every { postRepository.findOrThrow(capture(capturePost)){ RuntimeException("post not found")} }
            .answers { PostEntity(
                id = capturePost.captured,
                writer = UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ),
                title = "test post",
                code = "<hello code>",
                language = "<lang>",
                content = "<content>",
                writtenAt = null,
                tags = mutableSetOf(TagEntity(tag = "test-tag")),
                recommends = mutableSetOf(UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ))
            ) }

        assertThrows(RequiredFieldIsNullException::class.java) { postService.getPostById(1) }
    }
}