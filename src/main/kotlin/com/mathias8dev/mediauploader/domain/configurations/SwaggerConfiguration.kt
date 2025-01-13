package com.mathias8dev.mediauploader.domain.configurations

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Egolens backend",
        description = "Kotlin SpringWebflux Microservice",
        version = "1.0.0",
        contact = Contact(
            name = "Kossi Mathias KALIPE",
            email = "kalipemathias.pro@gmail.com",
            url = "https://github.com/mathias8dev"
        )
    )
)
@Configuration
class SwaggerConfiguration