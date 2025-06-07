package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.comment.CommentPostDto
import com.yareach.codesnaccbackend.dto.comment.CommentUpdateDto
import com.yareach.codesnaccbackend.entity.CommentEntity
import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.entity.UserRole
import com.yareach.codesnaccbackend.exception.CommentNotFoundException
import com.yareach.codesnaccbackend.exception.PostNotFoundException
import com.yareach.codesnaccbackend.exception.ResourceOwnershipException
import com.yareach.codesnaccbackend.exception.UserNotFoundException
import com.yareach.codesnaccbackend.repository.CommentRepository
import com.yareach.codesnaccbackend.repository.PostRepository
import com.yareach.codesnaccbackend.repository.UserRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime
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

    val mockUserId = "test-user1"

    val mockUserEntity = UserEntity(
        id = mockUserId,
        password = "<PASSWORD>",
        nickname = "<NICKNAME>",
        role = UserRole.USER,
    )

    val mockPostId = 0

    val mockPostEntity = PostEntity(
        id = mockPostId,
        writer = mockUserEntity,
        title = "<TITLE>",
        code = "<CODE>",
        language = "<LANGUAGE>",
        content = "<content>",
        tags = mutableSetOf(),
    )

    val mockCommentId = 1313
    val mockCommentEntity = CommentEntity(
        id = mockCommentId,
        content = "<CONTENT>",
        post = mockPostEntity,
        writer = mockUserEntity,
        writtenAt = LocalDateTime.now(),
        deleted = false
    )

    val newCommentId = 11223344
    val newCommentDto = CommentPostDto(
        content = "<TEST COMMENT>"
    )

    @Test
    @DisplayName("성공적으로 댓글을 올림")
    fun postComments() {
        every { userRepository.findByIdOrNull(mockUserId) } returns (mockUserEntity)

        every { postRepository.findByIdOrNull(mockPostId) } returns (mockPostEntity)

        val newCommentCaptor = slot<CommentEntity>()

        every { commentRepository.save(capture(newCommentCaptor)) } answers {
            newCommentCaptor.captured.apply {
                id = newCommentId
            }
        }

        val idOfGeneratedComment = commentService.postCommentByPostId(
            mockPostId, mockUserId, newCommentDto
        )

        assertEquals(newCommentId, idOfGeneratedComment)
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 실패함")
    fun failPostCommentCuzUserNotExists() {
        every { userRepository.findByIdOrNull(mockUserId) } returns (null)

        assertThrows(UserNotFoundException::class.java) {
            commentService.postCommentByPostId(mockPostId, mockUserId, newCommentDto)
        }
    }

    @Test
    @DisplayName("개시글이 존재하지 않는경우 실패함")
    fun postCommentButPostIsNotExists() {
        every { userRepository.findByIdOrNull(mockUserId) } returns (mockUserEntity)

        every { postRepository.findByIdOrNull(mockPostId) } returns (null)

        assertThrows(PostNotFoundException::class.java) {
            commentService.postCommentByPostId(
                mockPostId, mockUserId, newCommentDto
            )
        }
    }

    @Test
    @DisplayName("게시글 삭제 (성공하는 경우)")
    fun deleteComment() {
        val commentIdCapture = slot<Int>()
        every { commentRepository.findByIdOrNull(mockCommentId) } returns mockCommentEntity
        every { commentRepository.deleteById(capture(commentIdCapture)) } just runs

        commentService.deleteComment(mockCommentId, mockUserId)

        assertEquals(mockCommentId, commentIdCapture.captured)
    }

    @Test
    @DisplayName("댓글 삭제 (해당 댓글 존재하지 않아 실패)")
    fun tryDeleteCommentButTheCommentIsNotExists() {
        every { commentRepository.findByIdOrNull(mockCommentId) } returns null

        assertThrows(CommentNotFoundException::class.java) {
            commentService.deleteComment(mockCommentId, mockUserId)
        }
    }

    @Test
    @DisplayName("댓글 삭제 (해당 댓글이 다른 유저의 소유라 실패)")
    fun tryDeleteCommentButTheCommentsOwnerIsNotTestUser1() {
        val mockTestUser2 = UserEntity(
            id = "test-user2",
            password = "<PASSWORD>",
            nickname = "<NICKNAME>",
            role = UserRole.USER,
        )
        val mockCommentOfTestUser2 = CommentEntity(
            id = mockCommentId,
            content = "<COMMENT>",
            post = mockPostEntity,
            writer = mockTestUser2,
            writtenAt = LocalDateTime.now(),
        )

        every { commentRepository.findByIdOrNull(mockCommentId) } returns mockCommentOfTestUser2

        assertThrows(ResourceOwnershipException::class.java) {
            commentService.deleteComment(mockCommentId, mockUserId)
        }
    }

    @Test
    @DisplayName("id로 댓글 가져오기")
    fun getCommentById() {
        val commentIdCapture = slot<Int>()
        every { commentRepository.findByIdOrNull(capture(commentIdCapture)) } returns (mockCommentEntity)

        val result = commentService.getComment(mockCommentId)

        assertEquals(mockCommentId, result.commentId)
        assertEquals(mockCommentEntity.content, result.content)
        assertEquals(mockCommentEntity.writtenAt, result.writtenAt)
        assertEquals(mockCommentEntity.writer.id, result.writer.id)
    }

    @Test
    @DisplayName("id로 댓글 가져오기 (해당 댓글의 id가 존재하지 않음)")
    fun getCommentByIdButTheCommentIsNotExists() {
        every { commentRepository.findByIdOrNull(any()) } returns null

        assertThrows(CommentNotFoundException::class.java) {
            commentService.getComment(mockCommentId)
        }
    }

    @Test
    @DisplayName("댓글 수정")
    fun updateComment() {
        every { commentRepository.findByIdOrNull(mockCommentId) } returns mockCommentEntity

        assertEquals("<CONTENT>", mockCommentEntity.content)
        commentService.updateComment(mockCommentId, mockUserId, CommentUpdateDto( content = "<UPDATED CONTENT>" ))

        assertEquals("<UPDATED CONTENT>", mockCommentEntity.content)
    }

    @Test
    @DisplayName("댓글 수정(댓글 없어 실패)")
    fun tryUpdateCommentButTheCommentIsNotExists() {
        every { commentRepository.findByIdOrNull(mockCommentId) } returns null

        assertThrows(CommentNotFoundException::class.java) {
            commentService.updateComment(mockCommentId, mockUserId, CommentUpdateDto( content = "<UPDATED CONTENT>" ))
        }
    }

    @Test
    @DisplayName("댓글 수정(유저가 쓴 댓글이 아니라 실패)")
    fun tryUpdateCommentButTheCommentIsNotMine() {
        every { commentRepository.findByIdOrNull(mockCommentId) } returns mockCommentEntity

        assertThrows(ResourceOwnershipException::class.java) {
            commentService.updateComment(mockCommentId, "other-user", CommentUpdateDto( content = "<UPDATED CONTENT>" ))
        }
    }
}