package com.mathias8dev.mediauploader.domain.configurations

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing


@Configuration
@EnableR2dbcAuditing
class DatabaseConfig