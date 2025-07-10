package com.yareach.codesnaccbackend.service

import com.yareach.codesnaccbackend.entity.TagEntity
import com.yareach.codesnaccbackend.repository.TagRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class TagServiceImplTest {
    val tagRepositoryMock = mockk<TagRepository>()
    val tagService = TagServiceImpl(tagRepositoryMock)

    var isTagUpdated = false

    @BeforeEach
    fun setUpTagService() {
        tagService.readTagsFromDB()
    }

    @BeforeEach
    fun setUpTagRepository() {
        isTagUpdated = false

        every { tagRepositoryMock.findAll() }
            .answers {
                if(isTagUpdated)
                    mutableListOf(
                        TagEntity("tag1"),
                        TagEntity("tag2"),
                    )
                else
                    mutableListOf(
                        TagEntity("tag1")
                    )
            }
    }

    @Test
    fun getTagsTest() {
        val result = tagService.getTags()
        assertEquals(1, result.tags.size)
        assertEquals("tag1", result.tags[0])
    }

    @Test
    fun reloadTagTest() {
        val tagsBeforeUpdate = tagService.getTags()
        assertEquals(1, tagsBeforeUpdate.tags.size)
        assertEquals("tag1", tagsBeforeUpdate.tags[0])

        isTagUpdated = true
        tagService.readTagsFromDB()

        val tagsAfterUpdate = tagService.getTags()
        assertEquals(2, tagsAfterUpdate.tags.size)
        assertEquals("tag1", tagsAfterUpdate.tags[0])
        assertEquals("tag2", tagsAfterUpdate.tags[1])
    }
}