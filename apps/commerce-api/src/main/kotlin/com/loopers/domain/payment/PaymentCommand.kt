package com.loopers.domain.payment

import com.loopers.domain.type.PaymentType
import com.loopers.interfaces.api.order.OrderV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import java.math.BigDecimal

class PaymentCommand {

    data class Create(
        val payments: List<Payment>
    )

    data class Payment(
        val type: PaymentType,
        val amount: BigDecimal,
    ) {
        init {
            require(amount > BigDecimal.ZERO) { throw CoreException(ErrorType.INVALID_PAYMENT_PRICE) }
        }

        companion object {
            fun of(request: OrderV1Models.Request.Payment) = Payment(
                type = request.type,
                amount = request.amount,
            )
        }
    }
}
