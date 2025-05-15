package com.yareach.codesnaccbackend.exception

class PostNotFoundException(
    postId: Int
): RuntimeException("게시글이 존재하지 않습니다: $postId")