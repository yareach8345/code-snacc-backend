package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.PostCommentDto
import com.yareach.codesnaccbackend.entity.CommentEntity
import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.entity.UserRole
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.repository.CommentRepository
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.Test
import kotlin.test.assertEquals

class CommentServiceImplTest {
    val postRepository = mockk<PostRepository>()
    val userRepository = mockk<UserRepository>()
    val commentRepository = mockk<CommentRepository>()

    val commentService = CommentServiceImpl(
        commentRepository,
        userRepository,
        postRepository
    )

    @Test
    @DisplayName("성공적으로 댓글을 올림")
    fun postComments() {
        val postId = 0
        val userId = "test-user1"
        val newCommentDto = PostCommentDto(
            content = "<TEST COMMENT>"
        )
        val newCommentId = 11223344

        val mockUserEntity = UserEntity(
            id = userId,
            password = "<PASSWORD>",
            nickname = "<NICKNAME>",
            role = UserRole.USER,
        )

        val mockPostEntity = PostEntity(
            id = postId,
            writer = mockUserEntity,
            title = "<TITLE>",
            code = "<CODE>",
            language = "<LANGUAGe>",
            content = "<content>",
            tags = mutableSetOf(),
        )

        every { userRepository.findByIdOrNull(userId) } returns (mockUserEntity)

        every { postRepository.findByIdOrNull(postId) } returns (mockPostEntity)

        val newCommentCaptor = slot<CommentEntity>()

        every { commentRepository.save(capture(newCommentCaptor)) } answers {
            newCommentCaptor.captured.apply {
                id = newCommentId
            }
        }

        val idOfGeneratedComment = commentService.postCommentByPostId(
            postId, userId, newCommentDto
        )

        assertEquals(newCommentId, idOfGeneratedComment)
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 실패함")
    fun failPostCommentCuzUserNotExists() {
        val postId = 0
        val userId = "test-user1"
        val newCommentDto = PostCommentDto(
            content = "<TEST COMMENT>"
        )

        every { userRepository.findByIdOrNull(userId) } returns (null)

        assertThrows(UserNotFoundException::class.java) {
            commentService.postCommentByPostId(postId, userId, newCommentDto)
        }
    }

    @Test
    @DisplayName("개시글이 존재하지 않는경우 실패함")
    fun postCommentButPostIsNotExists() {
        val postId = 0
        val userId = "test-user1"
        val newCommentDto = PostCommentDto(
            content = "<TEST COMMENT>"
        )

        val mockUserEntity = UserEntity(
            id = userId,
            password = "<PASSWORD>",
            nickname = "<NICKNAME>",
            role = UserRole.USER,
        )

        every { userRepository.findByIdOrNull(userId) } returns (mockUserEntity)

        every { postRepository.findByIdOrNull(postId) } returns (null)

        assertThrows(PostNotFoundException::class.java) {
            commentService.postCommentByPostId(
                postId, userId, newCommentDto
            )
        }
    }
}