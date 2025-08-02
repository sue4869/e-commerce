package com.loopers.domain.payment

import com.loopers.domain.order.OrderItemDto

interface IPaymentCharger {

    fun charge(userId: String, orderItems: List<OrderItemDto>)
}
