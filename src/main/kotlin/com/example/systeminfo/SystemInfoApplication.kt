package com.example.systeminfo

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SystemInfoApplication {

    private val logger = LoggerFactory.getLogger(SystemInfoApplication::class.java)

    @Bean
    fun startupLogger(
        @Value("\${spring.application.name}") applicationName: String,
        @Value("\${app.version}") version: String,
        @Value("\${app.color}") color: String,
    ) = ApplicationRunner {
        logger.info(
            "event=application_started application={} version={} color={}",
            applicationName,
            version,
            color,
        )
    }
}

fun main(args: Array<String>) {
    runApplication<SystemInfoApplication>(*args)
}
