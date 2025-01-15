package com.mathias8dev.mediauploader.domain.services

import com.mathias8dev.mediauploader.models.Media
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.multipart.MultipartFile

interface FileStorageService {

    fun init()
    suspend fun save(file: MultipartFile): String
    suspend fun saveIn(dirname: String, file: MultipartFile): Media
    suspend fun saveIn(dirname: String, file: FilePart): Media
    suspend fun load(filename: String): Resource
    suspend fun loadIn(dirname: String, filename: String): Resource
    suspend fun deleteIn(dirname: String, filename: String)
    suspend fun deleteByPath(path: String)
    suspend fun saveIn(dirname: String, fileUrl: String): Media

}