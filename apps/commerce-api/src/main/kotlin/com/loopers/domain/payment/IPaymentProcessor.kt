package com.loopers.domain.payment

import com.loopers.domain.type.PaymentType
import java.math.BigDecimal

interface IPaymentProcessor {
    fun supportType(): PaymentType
    fun charge(userId: String, amount: BigDecimal)
}
