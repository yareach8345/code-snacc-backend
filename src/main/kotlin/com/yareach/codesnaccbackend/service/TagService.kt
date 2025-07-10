package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.tag.TagDto

interface TagService {
    fun getTags(): TagDto
}