package com.loopers.domain.event

import com.loopers.domain.log.AuditLogEntity

interface EventHandledRepository {
    fun save(eventHandledEntity: EventHandledEntity)

    fun existByMessage(message: String): Boolean
}
