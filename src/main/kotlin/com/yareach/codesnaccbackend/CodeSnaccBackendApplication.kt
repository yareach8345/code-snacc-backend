package com.yareach.codesnaccbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CodeSnaccBackendApplication

fun main(args: Array<String>) {
    runApplication<CodeSnaccBackendApplication>(*args)
}
