package com.mathias8dev.mediauploader.domain.utils

import org.apache.commons.io.FilenameUtils
import java.text.Normalizer

fun <T> T?.otherwise(value: T): T = this ?: value

inline fun <T> T?.otherwise(block: () -> T): T = this ?: block()

fun String.sanitize(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "")
    return FilenameUtils.normalize(normalized).replace(Regex("\\s+"), "_")
}