package com.yareach.codesnaccbackend.dto.post

enum class PostSearchOptionType {
    TITLE, WRITER, TAG, LANGUAGE
}

data class PostSearchOption(
    val searchBy: PostSearchOptionType,
    val searchValue: String
)
