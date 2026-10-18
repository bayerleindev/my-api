package com.example.systeminfo.domain

import java.time.Instant

data class SystemInfoResponse(
    val application: String,
    val version: String,
    val color: String,
    val hostname: String,
    val operatingSystem: OperatingSystemInfo,
    val java: JavaInfo,
    val runtime: RuntimeInfo,
    val timestamp: Instant,
)

data class OperatingSystemInfo(
    val name: String,
    val version: String,
    val architecture: String,
)

data class JavaInfo(
    val version: String,
    val vendor: String,
)

data class RuntimeInfo(
    val availableProcessors: Int,
    val maxMemoryBytes: Long,
)
