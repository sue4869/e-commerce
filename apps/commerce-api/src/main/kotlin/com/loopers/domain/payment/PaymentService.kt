package com.loopers.domain.payment

import com.loopers.domain.order.OrderItemDto
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val processors: List<IPaymentProcessor>
) {

    @Transactional
    fun charge(userId: String, orderItems: List<OrderItemDto>, paymentRequest: PaymentCommand.Create) {
        validateTotalPrice(orderItems.sumOf { it.totalPrice }, paymentRequest.payments.sumOf { it.amount })
        paymentRequest.payments.forEach { request ->
            val processor = processors.find { it.supportType() == request.type }
                ?: throw CoreException(
                    ErrorType.INVALID_PAYMENT_TYPE,
                    "지원하지 않는 결제 수단입니다: ${request.type}"
                )

            processor.charge(userId, request.amount)
        }
    }

    fun validateTotalPrice(totalOrderPrice: BigDecimal, totalPaymentAmount: BigDecimal) {
        if (totalOrderPrice != totalPaymentAmount) {
            throw CoreException(
                ErrorType.INVALID_PAYMENT_PRICE,
                "결제 금액이 총 주문 금액과 일치하지 않습니다. 주문 금액: $totalOrderPrice, 결제 총액: $totalPaymentAmount"
            )
        }
    }
}
