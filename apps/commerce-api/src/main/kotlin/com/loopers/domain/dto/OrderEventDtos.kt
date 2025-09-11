package com.loopers.domain.dto

import com.loopers.domain.event.EventPayload
import com.loopers.domain.event.EventType
import com.loopers.domain.type.OrderStatus

class PaidCompletedEvent(
    val orderUUId: String,
    val status: OrderStatus,
) : EventPayload

class PaidFailedEvent(
    val orderUUId: String,
    val status: OrderStatus,
) : EventPayload

data class StockFailedEvent(
    val orderUUId: String,
    val exception: Exception,
) : EventPayload

data class StockChangedEvent(
    val orderUUId: String,
) : EventPayload

data class OrderEvent(
    val orderUUId: String,
) : EventPayload

class OrderKafkaEvent(
    val orderUUId: String,
    val productId: Long? = null,
    val event: EventType,
    val message: String? = null,
) : EventPayload
