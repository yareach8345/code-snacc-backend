package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.tag.TagDto
import com.yareach.codesnaccbackend.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository
) : TagService {
    override fun getTags(): TagDto {
        val tags = tagRepository.findAll()
        return TagDto(
            tags = tags.map { it.tag }
        )
    }
}