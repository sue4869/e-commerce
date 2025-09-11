package com.loopers.interfaces.consumer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.loopers.domain.EventType
import com.loopers.domain.log.AuditLogEntity
import com.loopers.domain.log.AuditLogService
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class AuditLogEventConsumer(
    private val auditLogService: AuditLogService
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(
        topics = [
                    "\${application.kafka-topic.payment-event}",
                    "\${application.kafka-topic.product-like-event}",
                    "\${application.kafka-topic.stock-event}",
                 ],
        groupId = EventType.Group.AUDIT_LOG_EVENTS
    )
    fun handle(
        records: List<ConsumerRecord<String, String>>,
        acknowledgment: Acknowledgment
    ) {
        records.forEach { record ->
            val payloadJson = record.value()
            val payloadNode = jacksonObjectMapper().readTree(payloadJson)

            val orderUUId = payloadNode["orderUUId"]?.asText()
            val productId = payloadNode["productId"]?.asText()
            val eventType = payloadNode["event"]?.asText()
            val message = payloadNode["message"]?.asText()

            if (eventType != null && message != null) {

                val alreadyProcessed = auditLogService.isMessageProcessed(message)
                if (alreadyProcessed) {
                    log.info("Skipping duplicate message | message: $message")
                    return@forEach
                }

                // 이벤트 기록
                auditLogService.save(AuditLogEntity.of(
                    orderUUId = orderUUId,
                    productId = productId,
                    eventType = EventType.valueOf(eventType),
                    topic = record.topic(),
                    message = message
                    )
                )
            }
        }
        acknowledgment.acknowledge()
    }
}
