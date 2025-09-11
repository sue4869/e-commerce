package com.loopers.interfaces.consumer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.loopers.domain.EventType
import com.loopers.domain.event.EventHandledEntity
import com.loopers.domain.event.EventHandledService
import com.loopers.domain.metrics.ProductMetricsService
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.collections.forEach

@Component
class ProductMetricsConsumer(
    private val productMetricsService: ProductMetricsService,
    private val eventHandledService: EventHandledService
) {

    private val log = KotlinLogging.logger {}

    @KafkaListener(
        topics = [
            "\${application.kafka-topic.product-view}",
            "\${application.kafka-topic.product-like-event}",
            "\${application.kafka-topic.order-event}",
        ],
        groupId = EventType.Group.METRICS_EVENTS
    )
    fun handle(
        records: List<ConsumerRecord<String, String>>,
        acknowledgment: Acknowledgment
    ) {
        records.forEach { record ->
            val key = record.key()
            val payloadJson = record.value()
            val payloadNode = jacksonObjectMapper().readTree(payloadJson)

            val productId = payloadNode["productId"]?.asText()?.toLongOrNull() ?: throw IllegalArgumentException("Product ID could not be found")
            val eventType = payloadNode["event"]?.asText()?.let { EventType.valueOf(it) }
            val message = payloadNode["message"]?.asText()

            if (eventType != null && message != null) {

                val alreadyProcessed = eventHandledService.isMessageProcessed(message)
                if (alreadyProcessed) {
                    log.info("Skipping duplicate message | message: $message")
                    return@forEach
                }

                productMetricsService.handle(eventType, productId)
                eventHandledService.save(
                    EventHandledEntity.of(
                        key = key,
                    eventType = eventType,
                    topic = record.topic(),
                    message = message
                    )
                )
            }
        }
        acknowledgment.acknowledge()
    }
}
