package com.loopers.domain.payment

import com.loopers.domain.type.PaymentType
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class CardPaymentProcessor(
    private val pgClient: PgClient,
    private val afterPgProcessor: AfterPgProcessor,
) : IPaymentProcessor {

    override fun supportType(): PaymentType = PaymentType.CARD

    @Transactional
    @CircuitBreaker(name = "pgClient", fallbackMethod = "handleFailure")
    @Retry(name = "pgClientRetry", fallbackMethod = "handleFailure")
    override fun charge(orderUUId: String, userId: String, paymentInfo: PaymentCommand.Payment) {
        val request = PgRequest(
            orderId = orderUUId,
            cardType = paymentInfo.cardType!!.toString(),
            cardNo = paymentInfo.cardNo!!,
            amount = paymentInfo.amount,
            callbackUrl = "http://localhost:8080/api/v1/payment/after",
        )
        pgClient.requestPayment(userId, request)
    }

    open fun fallbackPayment(orderUUId: String, ex: Throwable) {
        afterPgProcessor.handleFailure(orderUUId, ex)
    }
}
