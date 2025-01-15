package com.mathias8dev.mediauploader.domain.utils


import org.apache.tika.detect.Detector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.apache.tika.mime.MimeType
import org.apache.tika.mime.MimeTypes
import org.apache.tika.parser.AutoDetectParser
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream

object MimeTypes {


    fun getRealMimeType(file: MultipartFile): String {
        return getRealMimeType(file.inputStream)
    }

    fun getRealMimeType(inputStream: InputStream): String {
        val parser = AutoDetectParser()
        val detector: Detector = parser.detector
        return try {
            val metadata = Metadata()
            val stream: TikaInputStream = TikaInputStream.get(inputStream)
            val mediaType: MediaType = detector.detect(stream, metadata)
            mediaType.toString()
        } catch (e: IOException) {
            MimeTypes.OCTET_STREAM
        }
    }

    fun mimeTypeFrom(file: MultipartFile): MimeType {
        val mimeTypeString = getRealMimeType(file.inputStream)
        return MimeTypes.getDefaultMimeTypes().forName(mimeTypeString)
    }

    fun mimeTypeFrom(inputStream: InputStream): MimeType {
        val mimeTypeString = getRealMimeType(inputStream)
        return MimeTypes.getDefaultMimeTypes().forName(mimeTypeString)
    }

    fun getExtensionOrNull(mimeTypeString: String): String? {
        return kotlin.runCatching {
            MimeTypes.getDefaultMimeTypes().forName(mimeTypeString).extension
        }.getOrNull()
    }

    fun getExtensionOrNull(file: MultipartFile): String? {
        return kotlin.runCatching {
            mimeTypeFrom(file).extension
        }.getOrNull()
    }

    fun getExtensionOrNull(inputStream: InputStream): String? {
        return kotlin.runCatching {
            mimeTypeFrom(inputStream).extension
        }.getOrNull()
    }
}