package com.loopers.application.payment

import com.loopers.domain.dto.OrderKafkaEvent
import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.KafkaEventPublisher
import com.loopers.domain.dto.PaidCompletedEvent
import com.loopers.domain.dto.PaidFailedEvent
import com.loopers.domain.event.EventType
import com.loopers.domain.payment.AfterPgProcessor
import com.loopers.domain.payment.PgAfterCommand
import com.loopers.domain.type.OrderStatus
import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentStatus.FAILED
import com.loopers.domain.type.PaymentStatus.PENDING
import com.loopers.domain.type.PaymentStatus.SUCCESS
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
open class PaymentFacade(
    private val afterPgProcessor: AfterPgProcessor,
    private val eventPublisher: EventPublisher,
    private val kafkaEventPublisher: KafkaEventPublisher,
    @Value("\${application.kafka-topic.payment-event:payment-event}") private val paymentKafkaTopicName: String,
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
                publishKafka(orderId, EventType.ORDER_PAID_COMPLETED)
            }
            FAILED -> {
                eventPublisher.publish(
                    PaidFailedEvent(
                        orderUUId = orderId,
                        status = OrderStatus.CANCELLED,
                    ),
                )
                log.info { "publish PaidFailedEvent orderId: $orderId, status: $paymentStatus" }
                publishKafka(orderId, EventType.ORDER_PAID_FAILED)
            }
        }
    }

    private fun publishKafka(orderId: String, event: EventType) {
        val messageKey = buildString {
            append(orderId)
            append(event.name)
            append(UUID.randomUUID())
        }

        kafkaEventPublisher.send(
            MessageBuilder
                .withPayload(
                    OrderKafkaEvent(
                        orderUUId = orderId,
                        event = event,
                        message = messageKey,
                    )
                )
                .setHeader(KafkaHeaders.TOPIC, paymentKafkaTopicName)
                .setHeader(KafkaHeaders.KEY, orderId)
                .build()
        )
    }
}
