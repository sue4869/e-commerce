package com.loopers.application.payment

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.PaidCompletedEvent
import com.loopers.domain.event.dto.PaidFailedEvent
import com.loopers.domain.payment.AfterPgProcessor
import com.loopers.domain.payment.PgAfterCommand
import com.loopers.domain.type.OrderStatus
import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentStatus.FAILED
import com.loopers.domain.type.PaymentStatus.PENDING
import com.loopers.domain.type.PaymentStatus.SUCCESS
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class PaymentFacade(
    private val afterPgProcessor: AfterPgProcessor,
    private val eventPublisher: EventPublisher,
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    open fun executeAfterPg(command: PgAfterCommand) {
        val status = afterPgProcessor.updatePaymentStatus(command)
        orderUpdateStatus(command.orderId, status)
    }

    private fun orderUpdateStatus(orderId: String, paymentStatus: PaymentStatus) {
        when (paymentStatus) {
            PENDING -> null
            SUCCESS -> {
                eventPublisher.publish(
                    PaidCompletedEvent(
                        orderUUId = orderId,
                        status = OrderStatus.PAID,
                    ),
                )
                log.info { "publish PaidCompletedEvent orderId: $orderId, status: $paymentStatus" }
            }
            FAILED -> {
                eventPublisher.publish(
                    PaidFailedEvent(
                        orderUUId = orderId,
                        status = OrderStatus.CANCELLED,
                    ),
                )
                log.info { "publish PaidFailedEvent orderId: $orderId, status: $paymentStatus" }
            }
        }
    }
}
