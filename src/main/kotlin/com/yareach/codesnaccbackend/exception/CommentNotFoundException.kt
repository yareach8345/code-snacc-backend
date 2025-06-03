package com.yareach.codesnaccbackend.exception

class CommentNotFoundException(
    val commentId: Int
): RuntimeException("해당 댓글이 존재하지 않습니다: $commentId")