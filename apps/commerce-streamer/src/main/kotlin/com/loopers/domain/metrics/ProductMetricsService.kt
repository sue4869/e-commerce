package com.loopers.domain.metrics

import com.loopers.domain.EventType
import org.springframework.stereotype.Service

@Service
class ProductMetricsService(
    private val processors: List<IMetricHandlerProcessor>
) {

    fun handle(eventType: EventType, productId: Long) {
        val handler = processors.find { it.supportType() == eventType}
            ?: throw IllegalArgumentException("No processor found for topic: ${eventType.topic}")

        handler.process(productId)
    }
}
