package com.loopers.interfaces.consumer

import com.loopers.domain.EventType

data class MetricKafkaEvent(
    val orderUUId: String,
    val productId: Long? = null,
    val eventType: EventType,
    val items: List<OrderItemEventDto>? = null,
    val message: String? = null,
)

data class OrderItemEventDto(
    val productId: Long,
    val price: Long,
    val qty: Int,
)
