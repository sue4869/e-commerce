package com.loopers.domain.event.dto

import com.loopers.domain.type.OrderStatus

data class OrderStatusChangeDto(
    val orderUUId: String,
    val status: OrderStatus
)
