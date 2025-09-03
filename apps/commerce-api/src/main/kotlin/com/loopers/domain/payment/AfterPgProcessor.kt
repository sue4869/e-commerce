package com.loopers.domain.payment

import com.loopers.domain.order.OrderRepository
import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentStatus.FAILED
import com.loopers.domain.type.PaymentStatus.PENDING
import com.loopers.domain.type.PaymentStatus.SUCCESS
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class AfterPgProcessor(
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val pgClient: PgClient,
) {

    fun handleFailure(orderUUId: String, ex: Throwable) {
        log.error("[CircuitBreaker] PG 결제 요청 실패. 이유=${ex.message}")
        handleByStatus(PaymentStatus.FAILED, orderUUId)
        throw CoreException(ErrorType.EXTERNAL_API_ERROR, "PG 결제 시스템이 불안정합니다. 잠시 후 다시 시도해주세요.")
    }

    @Transactional
    fun updatePaymentStatus(command: PgAfterCommand): PaymentStatus {
        val order = orderRepository.findByUuid(command.orderId) ?: throw CoreException(ErrorType.NOT_FOUND)
        val orderOfPg = pgClient.getPayment(order.userId, command.transactionKey).data ?: throw CoreException(ErrorType.NOT_FOUND)
        handleByStatus(orderOfPg.status, command.orderId)
        return orderOfPg.status
    }

    @Transactional
    fun handleByStatus(status: PaymentStatus, orderId: String) {
        val payments = paymentRepository.findByOrderUUId(orderId)
        payments.forEach { payment ->
            when (status) {
                PENDING -> null
                SUCCESS -> payment.updateStatus(FAILED)
                FAILED -> payment.updateStatus(SUCCESS)
            }
        }
        paymentRepository.saveAll(payments)
    }

    // TODO 추후 스케줄러를 이용해 배치작업한다.
    fun afterCareByBatch() {
    }
}
