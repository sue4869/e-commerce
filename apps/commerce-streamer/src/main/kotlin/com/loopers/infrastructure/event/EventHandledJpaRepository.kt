package com.loopers.infrastructure.event

import com.loopers.domain.event.EventHandledEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EventHandledJpaRepository: JpaRepository<EventHandledEntity, Long> {

    fun existByMessage(message: String): Boolean
}
