package com.mathias8dev.mediauploader.domain.beans

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mathias8dev.mediauploader.domain.services.FileStorageService
import com.mathias8dev.mediauploader.domain.services.LocalFileStorageService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.i18n.LocaleContextHolder.setTimeZone
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Configuration
class Beans {

    @Bean
    fun localFileStorageService(): FileStorageService {
        return LocalFileStorageService()
    }


    @Bean
    fun objectMapper(): ObjectMapper {
        val objectMapper = jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(JavaTimeModule().apply {
                addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
                addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME))
                setTimeZone(TimeZone.getTimeZone("UTC"))
            })

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Disable timestamp format
        return objectMapper
    }
}