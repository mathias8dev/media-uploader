package com.mathias8dev.mediauploader.repositories

import com.mathias8dev.mediauploader.models.Media
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MediaRepository : CoroutineCrudRepository<Media, Long> {
    suspend fun findByPath(path: String): Media?
}