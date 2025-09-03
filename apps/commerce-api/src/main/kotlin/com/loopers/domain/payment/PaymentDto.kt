package com.loopers.domain.payment

import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentType

data class PaymentDto(
    val orderUUId: String,
    val paymentKey: String?,
    val amount: Long,
    val type: PaymentType,
    val status: PaymentStatus,
) {
    companion object {
        fun of(source: PaymentEntity) = PaymentDto(
            orderUUId = source.orderUUId,
            paymentKey = source.paymentKey,
            amount = source.amount,
            type = source.type,
            status = source.status,
        )
    }
}
