package com.yareach.codesnaccbackend.dto.post

data class PostSearchDto (
    val title: String? = null,
    val writerId: String? = null,
    val tags: List<String>? = null,
    val language: String? = null
)