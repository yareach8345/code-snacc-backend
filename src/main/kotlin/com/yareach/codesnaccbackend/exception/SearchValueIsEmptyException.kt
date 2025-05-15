package com.yareach.codesnaccbackend.exception

class SearchValueIsEmptyException(
    key: String
): RuntimeException("${key}검색을 위한 값이 비어있습니다")