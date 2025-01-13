package com.mathias8dev.mediauploader.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/hello")
class HelloController {

    @GetMapping("/world")
    fun world(): String {
        return "Hello, World!"
    }

    @GetMapping("/exception")
    fun exception(): String {
        throw Exception("An error occurred")
    }
}