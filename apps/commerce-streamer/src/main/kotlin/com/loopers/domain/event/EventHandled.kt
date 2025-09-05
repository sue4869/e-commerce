package com.loopers.domain.event

import com.loopers.domain.BaseEntity
import com.loopers.domain.EventType
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "event_handled")
class EventHandled(
    val eventId: String,
    val eventType: EventType,
    val topic: String,
    val partitionNo: Int,
    val offsetNo: Long,
    val consumerGroup: String,
) : BaseEntity()
