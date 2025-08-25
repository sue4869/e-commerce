package com.loopers.domain.payment

import com.loopers.domain.order.OrderRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class PaymentService(
    private val processors: List<IPaymentProcessor>,
    private val paymentRepository: PaymentRepository
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    fun charge(orderUUId: String, userId: String, reqTotalPrice: Long, paymentRequest: PaymentCommand.Create) {
        validateTotalPrice(reqTotalPrice, paymentRequest.payments.sumOf { it.amount })
        paymentRequest.payments.forEach { request ->
            val processor = processors.find { it.supportType() == request.type }
                ?: throw CoreException(
                    ErrorType.INVALID_PAYMENT_TYPE,
                    "지원하지 않는 결제 수단입니다: ${request.type}"
                )
            try {
                processor.charge(orderUUId, userId, request)
                save(orderUUId, paymentRequest.payments)
            } catch (e: ObjectOptimisticLockingFailureException) {
                log.error { "[PaymentService] exception : ${e.message}" }
                throw CoreException(ErrorType.CONCURRENT_CONFLICT)
            }
        }
    }

    fun validateTotalPrice(totalOrderPrice: Long, totalPaymentAmount: Long) {
        if (totalOrderPrice != totalPaymentAmount) {
            throw CoreException(
                ErrorType.INVALID_PAYMENT_PRICE,
                "결제 금액이 총 주문 금액과 일치하지 않습니다. 주문 금액: $totalOrderPrice, 결제 총액: $totalPaymentAmount"
            )
        }
    }

    fun save(orderUUId: String, payments: List<PaymentCommand.Payment>) {
        paymentRepository.saveAll(payments.map { PaymentEntity.of(orderUUId, it) })
    }
}
