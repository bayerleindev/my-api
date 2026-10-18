package com.example.systeminfo.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.InetAddress
import java.time.Clock
import java.time.Instant

@Service
class SystemInfoService(
    @param:Value("\${spring.application.name}") private val applicationName: String,
    @param:Value("\${app.version}") private val version: String,
    @param:Value("\${app.color}") private val color: String,
    private val clock: Clock = Clock.systemUTC(),
) {

    fun current(): SystemInfoResponse {
        val runtime = Runtime.getRuntime()

        return SystemInfoResponse(
            application = applicationName,
            version = version,
            color = color,
            hostname = hostname(),
            operatingSystem = OperatingSystemInfo(
                name = systemProperty("os.name"),
                version = systemProperty("os.version"),
                architecture = systemProperty("os.arch"),
            ),
            java = JavaInfo(
                version = systemProperty("java.version"),
                vendor = systemProperty("java.vendor"),
            ),
            runtime = RuntimeInfo(
                availableProcessors = runtime.availableProcessors(),
                maxMemoryBytes = runtime.maxMemory(),
            ),
            timestamp = Instant.now(clock),
        )
    }

    private fun hostname(): String =
        System.getenv("HOSTNAME")
            ?.takeIf { it.isNotBlank() }
            ?: runCatching { InetAddress.getLocalHost().hostName }
                .getOrDefault("unknown")

    private fun systemProperty(name: String): String =
        System.getProperty(name, "unknown")
}
