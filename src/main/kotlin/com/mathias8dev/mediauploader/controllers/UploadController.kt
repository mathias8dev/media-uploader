package com.mathias8dev.mediauploader.controllers


import com.mathias8dev.mediauploader.domain.dto.media.UpdateMediaRequestDto
import com.mathias8dev.mediauploader.domain.services.FileStorageService
import com.mathias8dev.mediauploader.domain.utils.MimeTypes
import com.mathias8dev.mediauploader.domain.utils.otherwise
import com.mathias8dev.mediauploader.domain.utils.sanitize
import com.mathias8dev.mediauploader.models.Media
import com.mathias8dev.mediauploader.services.UploadService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api")
class UploadController(
    private val fileStorageService: FileStorageService,
    private val uploadService: UploadService,
) {


    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("data/files/download/**")
    suspend fun download(request: ServerHttpRequest): ResponseEntity<Resource> {
        val filePath = request.uri.toString().split("download/")[1].sanitize()
        val media = uploadService.findByPath(filePath).otherwise {
            throw IllegalArgumentException("The file with path $filePath does not exist")
        }
        logger.debug("The request url is {}", request.uri.toString())
        logger.debug("The request uri is ${request.uri}")
        logger.debug("The file path to load is $filePath")
        val file = fileStorageService.load(filePath)
        logger.debug("The real mime type is ${MimeTypes.getRealMimeType(file.inputStream)}")
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, """download; filename="${media.name}"""")
            .header(HttpHeaders.CONTENT_TYPE, media.mimeType)
            .body(file)
    }

    @GetMapping("data/files/view/**")
    suspend fun view(request: ServerHttpRequest): ResponseEntity<Resource> {
        val filePath = request.uri.toString().split("view/")[1].sanitize()
        val media = uploadService.findByPath(filePath).otherwise {
            throw IllegalArgumentException("The file with path $filePath does not exist")
        }
        logger.debug("The request url is {}", request.uri.toString())
        logger.debug("The request uri is ${request.uri}")
        logger.debug("The file path to load is $filePath")
        val file = fileStorageService.load(filePath)
        logger.debug("The real mime type is ${MimeTypes.getRealMimeType(file.inputStream)}")
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, """inline; filename="${media.name}"""")
            .header(HttpHeaders.CONTENT_TYPE, media.mimeType)
            .body(file)
    }


    @PostMapping("data/files/upload")
    suspend fun upload(
        @RequestPart(required = false) uploadedBy: String?,
        @RequestPart(required = false) name: String?,
        @RequestPart(required = false) title: String?,
        @RequestPart(required = false) description: String?,
        @RequestPart(required = false) altText: String?,
        @RequestPart file: Mono<FilePart>,
    ): Media {
        return uploadService.upload(
            uploadedBy = uploadedBy,
            name = name,
            title = title,
            description = description,
            altText = altText,
            file = file.awaitSingle()
        )
    }

    @PostMapping("data/files/delete")
    suspend fun deleteAllByIds(@RequestBody ids: Set<Long>) {
        uploadService.deleteAllByIds(ids)
    }

    @GetMapping("data/files")
    fun findAll(): Flow<Media> {
        return uploadService.findAll()
    }

    @PostMapping("data/files/update")
    suspend fun updateMedia(@RequestBody media: UpdateMediaRequestDto): Media {
        return uploadService.updateMedia(media)
    }
}