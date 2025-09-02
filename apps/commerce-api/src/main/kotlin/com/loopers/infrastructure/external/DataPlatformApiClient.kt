package com.loopers.infrastructure.external

import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class DataPlatformApiClient {

    private val log = KotlinLogging.logger {}

    fun send(data: String) {
        log.info("send Data: $data")
    }
}
