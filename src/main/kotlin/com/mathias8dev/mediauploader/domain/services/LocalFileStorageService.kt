package com.mathias8dev.mediauploader.domain.services


import com.mathias8dev.mediauploader.domain.utils.MimeTypes
import com.mathias8dev.mediauploader.domain.utils.Utils
import com.mathias8dev.mediauploader.domain.utils.otherwise
import com.mathias8dev.mediauploader.domain.utils.sanitize
import com.mathias8dev.mediauploader.models.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class LocalFileStorageService : FileStorageService {

    @Value("\${media-uploader.files-download-path}")
    private lateinit var downloadSubPath: String

    private var mediaName: String? = null
    private val root = Paths.get("uploads")
    private val logger = LoggerFactory.getLogger(this::class.java)


    private suspend fun getUrl(path: String): String {
        return "${Utils.baseServerUrl()}$downloadSubPath/${path}"
    }

    private suspend fun getMedia(filepath: String): Media {
        val mimeType = MimeTypes.getRealMimeType(this.root.resolve(filepath).toFile().inputStream())
        return Media(
            path = filepath,
            downloadUrl = getUrl(filepath),
            mimeType = mimeType,
            name = mediaName ?: ""
        )
    }

    init {
        init()
    }

    override fun init() {
        createDir(root)
    }

    override suspend fun save(file: MultipartFile): String {
        return saveIn(this.root, file)
    }

    override suspend fun saveIn(dirname: String, file: MultipartFile): Media {
        val path = "$dirname/${saveIn(this.root.resolve(dirname), file)}"
        return getMedia(path)
    }

    override suspend fun saveIn(dirname: String, file: FilePart): Media {
        val path = "$dirname/${saveIn(this.root.resolve(dirname), file)}"
        return getMedia(path)
    }


    override suspend fun saveIn(dirname: String, fileUrl: String): Media {
        val resource = try {
            UrlResource(fileUrl)
        } catch (e: MalformedURLException) {
            throw RuntimeException("An error occurred while trying to download the file from the URL", e)
        }

        if (!resource.exists() || !resource.isReadable) {
            throw RuntimeException("Failed to download or read the file from the URL")
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        resource.inputStream.use { input ->
            byteArrayOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        val byteArray = byteArrayOutputStream.toByteArray()

        // Save the downloaded file locally
        mediaName = resource.filename
        val resourceMimeType = MimeTypes.getRealMimeType(byteArray.inputStream())
        val extension = MimeTypes.getExtensionOrNull(resourceMimeType)
        val filename = safeMediaName(mediaName, extension)
        val filePath = "$dirname/$filename"
        val targetFile = File(this.root.resolve(filePath).toUri())
        try {
            byteArray.inputStream().use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException("An error occurred while saving the file locally", e)
        }


        return getMedia(filePath)

    }

    override suspend fun load(filename: String): Resource {
        return loadByPath(root.resolve(filename))
    }

    override suspend fun loadIn(dirname: String, filename: String): Resource {
        return load("$dirname/$filename")
    }

    override suspend fun deleteIn(dirname: String, filename: String) {
        try {
            val file = root.resolve("$dirname/$filename")
            withContext(Dispatchers.IO) {
                Files.deleteIfExists(file)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override suspend fun deleteByPath(path: String) {
        kotlin.runCatching {
            Files.deleteIfExists(root.resolve(path))
        }
    }

    private fun loadByPath(path: Path): Resource {
        try {
            val resource = UrlResource(path.toUri())
            if (resource.exists()) return resource
            throw RuntimeException("Impossible to read the file")
        } catch (e: MalformedURLException) {
            throw RuntimeException("An error occurred")
        }
    }

    private fun createDir(path: Path) {
        try {
            Files.createDirectories(path)
        } catch (e: FileAlreadyExistsException) {
            try {
                Files.delete(path)
                Files.createDirectories(path)
            } catch (e: Exception) {
                throw RuntimeException("FileStorageService error")
            }
        } catch (e: IOException) {
            throw RuntimeException("FileStorageService error")
        }
    }


    private fun saveIn(destination: Path, file: MultipartFile): String {
        createDir(destination)
        mediaName = file.originalFilename
        val filename: String = safeMediaName(mediaName)
        try {
            Files.copy(file.inputStream, destination.resolve(filename))
        } catch (e: Exception) {
            if (e is FileAlreadyExistsException) { // This will never occur
                throw RuntimeException(
                    "A file of that name already exists"
                )
            }

            throw RuntimeException(
                "An error occurred when trying to save your uploaded file"
            )
        }

        return filename
    }

    private suspend fun saveIn(destination: Path, file: FilePart): String {
        createDir(destination)
        mediaName = file.filename()
        val filename: String = safeMediaName(mediaName)
        try {
            logger.debug("The mediaName is $mediaName")
            file.transferTo(destination.resolve(filename)).awaitSingleOrNull()
        } catch (e: Exception) {
            if (e is FileAlreadyExistsException) { // This will never occur
                throw RuntimeException(
                    "A file of that name already exists"
                )
            }

            throw RuntimeException(
                "An error occurred when trying to save your uploaded file"
            )
        }

        return filename
    }

    private fun safeMediaName(name: String? = null, extension: String? = null): String {
        return "${Date().time}${name?.sanitize().otherwise(extension ?: "unknown")}"
    }
}