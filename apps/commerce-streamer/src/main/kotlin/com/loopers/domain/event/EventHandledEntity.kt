package com.loopers.domain.event

import com.loopers.domain.BaseEntity
import com.loopers.domain.EventType
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "event_handled")
class EventHandledEntity(
    key: String,
    eventType: EventType,
    topic: String,
    message: String?,
) : BaseEntity() {

    val key: String = key
    val eventType: EventType = eventType
    val topic: String = topic
    val message: String? = message

    companion object {
        fun of(
            key: String,
            eventType: EventType,
            topic: String,
            message: String?,
        ): EventHandledEntity {
            return EventHandledEntity(
                key = key,
                eventType = eventType,
                topic = topic,
                message = message
            )
        }
    }
}
