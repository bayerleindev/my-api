package com.example.systeminfo.observability

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.time.TimeSource

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val startedAt = TimeSource.Monotonic.markNow()

        try {
            filterChain.doFilter(request, response)
        } finally {
            log.info(
                "event=http_request method={} path={} status={} duration_ms={}",
                request.method,
                request.requestURI,
                response.status,
                startedAt.elapsedNow().inWholeMilliseconds,
            )
        }
    }
}
