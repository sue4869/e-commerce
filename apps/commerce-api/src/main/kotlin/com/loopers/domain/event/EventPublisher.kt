package com.loopers.domain.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class EventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun publish(event: EventPayload) {
        applicationEventPublisher.publishEvent(event)
    }
}
