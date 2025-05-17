package com.yareach.codesnaccbackend.dto.post

data class PostUploadDto (
    val writerId: String,
    val title: String,
    val code: String,
    val language: String,
    val content: String,
    val tags: List<String>,
)