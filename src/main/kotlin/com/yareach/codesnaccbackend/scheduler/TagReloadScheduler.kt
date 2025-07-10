package com.yareach.codesnaccbackend.scheduler

import com.yareach.codesnaccbackend.extensions.logger
import com.yareach.codesnaccbackend.service.TagServiceImpl
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class TagReloadScheduler(
    private val tagService: TagServiceImpl
) {
    val logger = logger()

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    fun run() {
        logger.info("Read tags from DB")

        tagService.readTagsFromDB()
    }
}