package com.loopers.domain.log

import com.loopers.domain.BaseEntity
import com.loopers.domain.EventType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "event_log")
class AuditLogEntity(
    orderUUId: String? = null,
    productId: String? = null,
    eventType: EventType,
    topic: String,
    message: String,
) : BaseEntity() {

    val orderUUId: String? = orderUUId
    val productId: String? = productId
    @Enumerated(EnumType.STRING)
    val eventType: EventType = eventType
    val topic: String = topic
    val message: String = message

    companion object {
        fun of(
            orderUUId: String?,
            productId: String?,
            eventType: EventType,
            topic: String,
            message: String,
        ):AuditLogEntity {
            return AuditLogEntity(
            orderUUId = orderUUId,
            productId = productId,
            eventType = eventType,
            topic = topic,
            message = message,
            )
        }
    }
}
