package com.loopers.domain.payment

import com.loopers.domain.type.PaymentType

interface IPaymentProcessor {
    fun supportType(): PaymentType
    fun charge(orderUUId: String, userId: String, paymentInfo: PaymentCommand.Payment)
}
