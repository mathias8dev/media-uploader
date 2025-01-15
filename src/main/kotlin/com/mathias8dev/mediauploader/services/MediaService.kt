package com.mathias8dev.mediauploader.services


import com.mathias8dev.mediauploader.domain.services.FileStorageService
import com.mathias8dev.mediauploader.models.Media
import com.mathias8dev.mediauploader.repositories.MediaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service


@Service
class MediaService(
    private val mediaRepository: MediaRepository,
    private val fileStorageService: FileStorageService
) {

    suspend fun findById(mediaId: Long): Media? {
        return mediaRepository.findById(mediaId)
    }

    suspend fun findByPath(path: String): Media? {
        return mediaRepository.findByPath(path)
    }

    fun findAll(): Flow<Media> {
        return mediaRepository.findAll()
    }

    fun findAllById(mediaIds: Set<Long>): Flow<Media> {
        return mediaRepository.findAllById(mediaIds)
    }

    suspend fun save(media: Media): Media {
        return mediaRepository.save(media)
    }

    suspend fun deleteAllByIds(mediaIds: Set<Long>) = coroutineScope {
        mediaRepository.findAllById(mediaIds).toList().map {
            async { delete(it) }
        }.awaitAll()
    }


    suspend fun deleteById(mediaId: Long) {
        mediaRepository.findById(mediaId)?.let {
            delete(it)
        }
    }

    suspend fun delete(media: Media) {
        fileStorageService.deleteByPath(media.path)
        mediaRepository.delete(media)
    }
}