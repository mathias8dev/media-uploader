package com.mathias8dev.mediauploader.services


import com.mathias8dev.mediauploader.domain.dto.media.UpdateMediaRequestDto
import com.mathias8dev.mediauploader.domain.services.FileStorageService
import com.mathias8dev.mediauploader.domain.utils.otherwise
import com.mathias8dev.mediauploader.models.Media
import kotlinx.coroutines.flow.Flow
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File


@Service
@Transactional
class UploadService(
    private val mediaService: MediaService,
    private val storageService: FileStorageService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Value("\${media-uploader.upload.dir}")
    private lateinit var uploadDir: String


    suspend fun upload(
        uploadedBy: String?,
        name: String?,
        title: String?,
        description: String?,
        altText: String?,
        file: FilePart,
    ): Media {
        val media = storageService.saveIn(uploadDir, file)
        val resource = storageService.load(media.path)
        media.name = name ?: media.name.takeIf { it.isNotBlank() } ?: resource.filename ?: File(media.path).name
        media.title = title
        media.size = resource.contentLength()
        media.description = description
        media.altText = altText
        media.uploadedBy = uploadedBy ?: "anonymous"
        return mediaService.save(media)
    }

    suspend fun deleteAllByIds(ids: Set<Long>) {
        mediaService.deleteAllByIds(ids)
    }

    suspend fun findByPath(path: String): Media? {
        return mediaService.findByPath(path)
    }

    fun findAll(): Flow<Media> {
        return mediaService.findAll()
    }

    suspend fun updateMedia(media: UpdateMediaRequestDto): Media {
        val existing = mediaService.findById(media.id).otherwise { throw IllegalArgumentException("Media not found") }
        val updated = existing.copy()
        media.name?.let { updated.name = it }
        media.title?.let { updated.title = it }
        media.description?.let { updated.description = it }
        media.altText?.let { updated.altText = it }
        media.uploadedBy?.let { updated.uploadedBy = it }
        return if (updated != existing) mediaService.save(updated) else existing
    }

}