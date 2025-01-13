package com.mathias8dev.mediauploader.advices

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionAdvice(
    private val env: Environment
) : ResponseEntityExceptionHandler() {
    @ExceptionHandler(value = [Exception::class])
    protected fun handleGlobalException(
        ex: Exception, exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {

        val body = getErrorsBody(ex, exchange, HttpStatus.INTERNAL_SERVER_ERROR)
        return handleExceptionInternal(
            ex,
            body,
            HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            exchange
        )
    }

    private fun getErrorsBody(exception: Exception, exchange: ServerWebExchange, status: HttpStatus): Map<String, String> {
        val body = mutableMapOf<String, String>()
        body["timestamp"] = LocalDateTime.now().toString()
        body["path"] = exchange.request.uri.path
        body["status"] = status.value().toString()
        body["error"] = status.reasonPhrase
        body["message"] = exception.message ?: "An error occurred"
        body["exception"] = exception.javaClass.name
        if (env["debug"].toBoolean()) body["trace"] = exception.stackTraceToString()

        return body
    }
}