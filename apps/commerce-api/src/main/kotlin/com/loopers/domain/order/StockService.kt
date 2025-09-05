package com.loopers.domain.order

import com.loopers.domain.dto.OrderKafkaEvent
import com.loopers.domain.event.EventPublisher
import com.loopers.domain.dto.StockFailedEvent
import com.loopers.domain.event.EventType
import com.loopers.domain.event.KafkaEventPublisher
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.type.OrderStatus
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class StockService(
    private val productRepository: ProductRepository,
    private val orderItemRepository: OrderItemRepository,
    private val eventPublisher: EventPublisher,
    private val kafkaEventPublisher: KafkaEventPublisher,
    @Value("\${application.kafka-topic.stock-event:stock-event}") private val stockKafkaTopicName: String,
) {

    private val log = KotlinLogging.logger {}

    @Transactional
    fun reduceStock(orderUUId: String, orderStatus: OrderStatus) {
        if (orderStatus != OrderStatus.PAID) {
            throw CoreException(ErrorType.ONLY_AFTER_PAID)
        }

        try {
            val items = orderItemRepository.findByOrderUUId(orderUUId)
            if (items.isEmpty()) {
                throw CoreException(ErrorType.NOT_FOUND, "해당 주문 아이템을 찾을 수 없습니다. orderUUId: $orderUUId")
            }

            val idToProduct = productRepository.findByIdInWithPessimisticLock(
                items.map { it.productId }.sorted(),
            ).associateBy { it.id }

            val updatedProducts = items.map { item ->
                val product = idToProduct[item.productId]
                    ?: throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "상품이 존재하지 않습니다. id=${item.productId}")
                updateStock(orderUUId, product, item.qty)
                publishKafka(orderUUId = orderUUId, event = EventType.STOCK_CHANGE_FAILED)
                product
            }

            productRepository.saveAll(updatedProducts)
        } catch (ex: Exception) {
            eventPublisher.publish(StockFailedEvent(orderUUId, ex))
            log.info("publish StockFailedEvent orderUUId: $orderUUId error: ${ex.message}")
            publishKafka(orderUUId = orderUUId, event = EventType.STOCK_CHANGE_FAILED)
            throw ex
        }
    }

    fun updateStock(orderUUId: String, product: ProductEntity, qty: Int) {
        validateStock(product.stock, qty)
        val updatedStock = product.updateStock(qty)
        if (updatedStock == 0) {
            publishKafka(orderUUId = orderUUId, productId = product.id, event = EventType.STOCK_SOLD_OUT)
        }
        publishKafka(orderUUId = orderUUId, productId = product.id, event = EventType.STOCK_CHANGED)
    }

    fun validateStock(stock: Int, qty: Int) {
        if (stock < qty) {
            throw CoreException(ErrorType.OUT_OF_STOCK)
        }
    }

    private fun publishKafka(orderUUId: String, productId: Long? = null, event: EventType) {
        val messageKey = buildString {
            append(orderUUId)
            append(event.name)
            append(UUID.randomUUID())
        }

        kafkaEventPublisher.send(
            MessageBuilder
                .withPayload(
                    OrderKafkaEvent(
                        orderUUId = orderUUId,
                        productId = productId,
                        event = event,
                        message = messageKey,
                    )
                )
                .setHeader(KafkaHeaders.TOPIC, stockKafkaTopicName)
                .setHeader(KafkaHeaders.KEY, productId)
                .build()
        )
    }
}
