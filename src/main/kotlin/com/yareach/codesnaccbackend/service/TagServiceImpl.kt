package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.dto.tag.TagDto
import com.yareach.codesnaccbackend.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class TagServiceImpl(
    private val tagRepository: TagRepository
) : TagService {
    val tags: MutableSet<String> = mutableSetOf()

    override fun getTags(): TagDto {
        return TagDto(
            tags = tags.toList()
        )
    }

    fun readTagsFromDB() {
        tags.clear()
        tagRepository
            .findAll()
            .map{it.tag}
            .forEach { tags.add(it) }
    }
}