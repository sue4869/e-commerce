package com.loopers.infrastructure.event

import com.loopers.domain.event.EventHandledEntity
import com.loopers.domain.event.EventHandledRepository
import org.springframework.stereotype.Component

@Component
class EventHandledJpaRepositoryImpl(
    private val eventHandledJpaRepository: EventHandledJpaRepository
): EventHandledRepository {

    override fun save(eventHandledEntity: EventHandledEntity) {
        eventHandledJpaRepository.save(eventHandledEntity)
    }

    override fun existByMessage(message: String): Boolean {
        return eventHandledJpaRepository.existByMessage(message)
    }

}
