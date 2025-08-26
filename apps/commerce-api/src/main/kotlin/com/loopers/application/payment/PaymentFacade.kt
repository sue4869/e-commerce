package com.loopers.application.payment

import com.loopers.domain.event.dto.OrderStatusChangeDto
import com.loopers.domain.payment.AfterPgProcessor
import com.loopers.domain.payment.PgAfterCommand
import com.loopers.domain.type.OrderStatus
import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentStatus.FAILED
import com.loopers.domain.type.PaymentStatus.PENDING
import com.loopers.domain.type.PaymentStatus.SUCCESS
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class PaymentFacade(
    private val afterPgProcessor: AfterPgProcessor,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional
    open fun executeAfterPg(command: PgAfterCommand) {
        val status = afterPgProcessor.executeAfterPg(command)
        orderUpdateStatus(command.orderId, status)
    }

    private fun orderUpdateStatus(orderId: String, paymentStatus: PaymentStatus) {
        val status = when (paymentStatus) {
            PENDING -> OrderStatus.PAYMENT_PENDING
            SUCCESS -> OrderStatus.PAID
            FAILED -> OrderStatus.CANCELLED
        }

        applicationEventPublisher.publishEvent(
            OrderStatusChangeDto(
                orderUUId = orderId, status = status
            )
        )
    }
}
