package com.mathias8dev.mediauploader.models

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("media")
data class Media(
    @Id
    var id: Long = 0,
    var mimeType: String,
    var downloadUrl: String,
    @JsonProperty("uploadedTo")
    var path: String,
    var uploadedBy: String? = null,
    var name: String,
    var title: String? = null,
    var description: String? = null,
    var altText: String? = null,
    var size: Long = 0,
    @JsonProperty("uploadDate")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)