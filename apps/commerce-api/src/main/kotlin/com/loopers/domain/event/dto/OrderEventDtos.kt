package com.loopers.domain.event.dto

import com.loopers.domain.event.EventPayload
import com.loopers.domain.type.OrderStatus

class PaidCompletedEvent(
    val orderUUId: String,
    val status: OrderStatus
): EventPayload

class PaidFailedEvent(
    val orderUUId: String,
    val status: OrderStatus
): EventPayload

data class StockFailedEvent(
    val orderUUId: String,
    val exception: Exception
): EventPayload
