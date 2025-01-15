package com.mathias8dev.mediauploader.domain.utils

import com.mathias8dev.mediauploader.domain.beans.request.ReactiveRequestContextHolder
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest

object Utils {

    private val logger = LoggerFactory.getLogger(Utils::class.java)

    fun baseServerUrl(request: ServerHttpRequest): String {
        return request.uri.scheme + "://" + request.uri.host + ":" + request.uri.port
    }

    suspend fun baseServerUrl(): String? {
        return ReactiveRequestContextHolder.get()?.let { baseServerUrl(it) }
    }
}