package com.loopers.domain.event

import com.loopers.domain.metrics.ProductMetricsService
import org.springframework.stereotype.Service

@Service
class EventHandledService(
    private val eventHandledRepository: EventHandledRepository
) {
    fun save(eventHandledEntity: EventHandledEntity) {
        eventHandledRepository.save(eventHandledEntity)
    }

    fun isMessageProcessed(message: String): Boolean {
        return eventHandledRepository.existByMessage(message)
    }
}
