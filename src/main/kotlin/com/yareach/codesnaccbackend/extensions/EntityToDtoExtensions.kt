package com.yareach.codesnaccbackend.extensions

import com.yareach.codesnaccbackend.config.security.CustomUserDetails
import com.yareach.codesnaccbackend.dto.comment.CommentDto
import com.yareach.codesnaccbackend.dto.post.PostInfoResponseDto
import com.yareach.codesnaccbackend.dto.user.UserInfoDto
import com.yareach.codesnaccbackend.dto.user.WriterInfoDto
import com.yareach.codesnaccbackend.entity.CommentEntity
import com.yareach.codesnaccbackend.entity.PostEntity
import com.yareach.codesnaccbackend.entity.UserEntity
import com.yareach.codesnaccbackend.exception.RequiredFieldIsNullException

fun UserEntity.toUserInfoDto() = UserInfoDto(id, nickname, role.name, banned, quit, warnCnt, icon)

fun UserEntity.toCustomUserDetails() = CustomUserDetails(this)

fun UserEntity.toWriterInfoDto() = WriterInfoDto(
    id = id,
    nickname = nickname,
    icon = icon ?: throw RequiredFieldIsNullException("icon", "UserEntity")
)

fun PostEntity.toDto(userId: String?) =
    PostInfoResponseDto(
        id = id ?: throw RequiredFieldIsNullException("id", "PostEntity"),
        writer = writer.toWriterInfoDto(),
        title = title,
        code = code,
        language = language,
        content = content,
        writtenAt = writtenAt ?: throw RequiredFieldIsNullException("writtenAt", "PostEntity"),
        tags = getTagList(),
        commentCnt = comments.size,
        recommendCnt = getRecommendCount(),
        didIRecommend = userId in recommends.map { user -> user.id }
    )

fun CommentEntity.toDto() =
    CommentDto(
        commentId = id ?: throw RequiredFieldIsNullException("id", "CommentEntity"),
        content = content,
        writer = writer.toWriterInfoDto(),
        writtenAt = writtenAt ?: throw RequiredFieldIsNullException("writtenAt", "CommentEntity"),
    )