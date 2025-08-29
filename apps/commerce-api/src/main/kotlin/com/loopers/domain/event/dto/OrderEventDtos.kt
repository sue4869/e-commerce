package com.loopers.domain.event.dto

import com.loopers.domain.event.EventPayload
import com.loopers.domain.type.OrderStatus

class PaidCompleteEvent(
    val orderUUId: String,
    val status: OrderStatus
): EventPayload

class PaidFailEvent(
    val orderUUId: String,
    val status: OrderStatus
): EventPayload

data class StockFailedEvent(
    val orderUUId: String,
    val exception: Exception
): EventPayload
