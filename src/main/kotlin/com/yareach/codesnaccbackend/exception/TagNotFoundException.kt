package com.yareach.codesnaccbackend.exception

class TagNotFoundException (
    val tag: String
): RuntimeException("해당 태그는 존재하지 않습니다: $tag")