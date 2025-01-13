package com.mathias8dev.mediauploader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MediaUploaderApplication

fun main(args: Array<String>) {
    runApplication<MediaUploaderApplication>(*args)
}
