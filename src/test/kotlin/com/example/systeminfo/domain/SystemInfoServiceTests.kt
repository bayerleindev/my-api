package com.example.systeminfo.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SystemInfoServiceTests {

    @Test
    fun `returns configured blue version and system information`() {
        val instant = Instant.parse("2026-10-18T12:00:00Z")
        val service = SystemInfoService(
            applicationName = "system-info-api",
            version = "blue",
            color = "blue",
            clock = Clock.fixed(instant, ZoneOffset.UTC),
        )

        val result = service.current()

        assertThat(result.application).isEqualTo("system-info-api")
        assertThat(result.version).isEqualTo("blue")
        assertThat(result.color).isEqualTo("blue")
        assertThat(result.hostname).isNotBlank()
        assertThat(result.operatingSystem.name).isNotBlank()
        assertThat(result.java.version).isNotBlank()
        assertThat(result.runtime.availableProcessors).isPositive()
        assertThat(result.timestamp).isEqualTo(instant)
    }
}
