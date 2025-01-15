package com.mathias8dev.mediauploader.domain.dto.media

data class UpdateMediaRequestDto(
    val id: Long = 0,
    val uploadedBy: String? = null,
    val name: String?,
    val title: String? = null,
    val description: String? = null,
    val altText: String? = null,
)
