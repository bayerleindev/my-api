package com.example.systeminfo.api

import com.example.systeminfo.domain.SystemInfoResponse
import com.example.systeminfo.domain.SystemInfoService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SystemInfoController(
    private val systemInfoService: SystemInfoService,
) {

    private val logger = LoggerFactory.getLogger(SystemInfoController::class.java)

    @GetMapping("/", "/api/system")
    fun systemInfo(): SystemInfoResponse {
        val response = systemInfoService.current()

        logger.info(
            "event=system_info_requested version={} color={} hostname={}",
            response.version,
            response.color,
            response.hostname,
        )

        return response
    }
}
