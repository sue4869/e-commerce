package com.loopers.domain.payment

import com.loopers.domain.order.OrderItemDto
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal


@Service
class PaymentService(
    private val processors: List<IPaymentProcessor>
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    fun charge(userId: String, reqTotalPrice: BigDecimal, paymentRequest: PaymentCommand.Create) {
        validateTotalPrice(reqTotalPrice, paymentRequest.payments.sumOf { it.amount })
        paymentRequest.payments.forEach { request ->
            val processor = processors.find { it.supportType() == request.type }
                ?: throw CoreException(
                    ErrorType.INVALID_PAYMENT_TYPE,
                    "지원하지 않는 결제 수단입니다: ${request.type}"
                )
            try {
                processor.charge(userId, request.amount)
            } catch (e: ObjectOptimisticLockingFailureException) {
                log.error { "[PaymentService] exception : ${e.message}" }
                throw CoreException(ErrorType.CONCURRENT_CONFLICT)
            }
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
