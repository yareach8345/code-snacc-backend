package com.yareach.codesnaccbackend.dto.post

data class SearchPostResultDto (
    val posts: List<PostInfoResponseDto>,
    val numberOfPosts: Int,
    val pageNumber: Int,
    val pageSize: Int
)