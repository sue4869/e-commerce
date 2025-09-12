package com.loopers.interfaces.consumer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.loopers.domain.EventType
import com.loopers.domain.event.EventHandledEntity
import com.loopers.domain.event.EventHandledService
import com.loopers.domain.metrics.ProductMetricsService
import com.loopers.domain.ranking.ProductRankingService
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.collections.forEach

@Component
class ProductMetricsConsumer(
    private val productMetricsService: ProductMetricsService,
    private val eventHandledService: EventHandledService,
    private val productRankingService: ProductRankingService,
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
            val event = jacksonObjectMapper().readValue(payloadJson, MetricKafkaEvent::class.java)

            val productId = event.productId ?: throw IllegalArgumentException("Product ID could not be found")
            val eventType = event.eventType
            val message = event.message ?: throw IllegalArgumentException("Message could not be found")

            val alreadyProcessed = eventHandledService.isMessageProcessed(message)
            if (alreadyProcessed) {
                log.info("Skipping duplicate message | message: $message")
                return@forEach
            }

            productMetricsService.handle(eventType, productId)
            productRankingService.rank(event)
            eventHandledService.save(
                EventHandledEntity.of(
                    key = key,
                eventType = eventType,
                topic = record.topic(),
                message = message
                )
            )
        }
        acknowledgment.acknowledge()
    }
}
