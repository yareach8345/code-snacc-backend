package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.post.PostUploadDto
import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.TagEntity
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.entity.UserRole
import com.yareach.codesnaccbackend.exception.RequiredFieldIsNullException
import com.yareach.codesnaccbackend.exception.ResourceOwnershipException
import com.yareach.codesnaccbackend.extensions.findOrThrow
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.TagRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
import java.util.Optional

class PostServiceImplTest {
    val postRepository = mockk<PostRepository>()
    val userRepository = mockk<UserRepository>()
    val tagRepository = mockk<TagRepository>()

    val postService = PostServiceImpl(
        postRepository,
        userRepository,
        tagRepository
    )

    @Test
    @DisplayName("N개의 post 조회 테스트")
    fun searchPosts() {
        val capturePost = slot<Pageable>()
        every { postRepository.findPostsBySearchCondition(pageable = capture(capturePost)) }
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
        every { postRepository.countBySearchCondition() }.answers { 100 }

        // 10개를 한 페이지의 크기로. 첫번째 페이지를 가져옴
        val firstPageResult = postService.searchPosts(10, 0, null)
        val postsOnFirstPage = firstPageResult.posts

        assertEquals(100, firstPageResult.numberOfPosts)

        assertEquals(10, firstPageResult.posts.size)

        assertEquals(0, capturePost.captured.pageNumber)
        assertEquals(10, capturePost.captured.pageSize)

        assertEquals(10, postsOnFirstPage.size)
        assertEquals(0, postsOnFirstPage.first().id)
        assertEquals(9, postsOnFirstPage.last().id)

        // 5개를 한 페이지의 크기로. 세번째 페이지를 가져옴
        val thirdPageResult = postService.searchPosts(5, 2, null)
        val postsOnThirdPage = thirdPageResult.posts

        assertEquals(100, firstPageResult.numberOfPosts)

        assertEquals(5, thirdPageResult.posts.size)

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

    @Test
    @DisplayName("post 작성 테스트")
    fun uploadPost() {
        val capturePost = slot<PostEntity>()
        val captureTag = slot<Collection<String>>()
        every { postRepository.save(capture(capturePost)) }.answers {
            capturePost
                .captured
                .apply {
                    id = 0
                }
        }

        every { userRepository.findById("test-user1") }.answers {
            UserEntity(
                id = "test-user1",
                password = "<PASSWORD>",
                role = UserRole.USER,
            ).let { Optional.of(it) }
        }

        every { tagRepository.findByTagIn(capture(captureTag)) }.answers {
            captureTag.captured.map { TagEntity(tag = it) }.toMutableSet()
        }

        val postUploadDto = PostUploadDto(
            writerId = "test-user1",
            title = "test-title",
            code = "test-code",
            language = "test-language",
            content = "test-content",
            tags = listOf("test-tag1", "test-tag2")
        )

        postService.uploadPost(postUploadDto)

        assertEquals("test-user1", capturePost.captured.writer.id)
        assertEquals("test-title", capturePost.captured.title)
        assertEquals("test-code", capturePost.captured.code)
        assertEquals("test-language", capturePost.captured.language)
        assertEquals("test-content", capturePost.captured.content)
        assertEquals(2, capturePost.captured.tags.size)
        assertTrue { capturePost.captured.tags.map { it.tag }.let{
            "test-tag1" in it && "test-tag2" in it
        }}
    }

    @Test
    @DisplayName("post 삭제 테스트")
    fun deletePost() {
        val capturePostId = slot<Int>()

        every { postRepository.findByIdOrNull(1)  }.returns(
            PostEntity(
                id = 1,
                writer = UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ),
                title = "<TITLE>",
                code = "<CODE>",
                language = "<LANGUAGE>",
                content = "<CONTENT>",
                tags = mutableSetOf(),
            )
        )

        every { postRepository.deleteById(capture(capturePostId)) }.returns(Unit)

        postService.deletePost(1, "testuser")

        assertEquals(1, capturePostId.captured)
    }

    @Test
    @DisplayName("다른 사람의 ID로 삭제불가")
    fun deletePostWithWrongId() {
        val capturePostId = slot<Int>()

        every { postRepository.findByIdOrNull(1)  }.returns(
            PostEntity(
                id = 1,
                writer = UserEntity(
                    id = "testuser",
                    password = "<PASSWORD>",
                    role = UserRole.USER,
                ),
                title = "<TITLE>",
                code = "<CODE>",
                language = "<LANGUAGE>",
                content = "<CONTENT>",
                tags = mutableSetOf(),
            )
        )

        every { postRepository.deleteById(capture(capturePostId)) }.returns(Unit)

        assertThrows(ResourceOwnershipException::class.java){ postService.deletePost(1, "aWrongId") }
    }
}