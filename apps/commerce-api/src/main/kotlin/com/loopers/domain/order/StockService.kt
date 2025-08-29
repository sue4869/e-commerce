package com.loopers.domain.order

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.StockFailedEvent
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.type.OrderStatus
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StockService(
    private val productRepository: ProductRepository,
    private val orderItemRepository: OrderItemRepository,
    private val eventPublisher: EventPublisher,
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
                items.map { it.productId }.sorted()
            ).associateBy { it.id }

            val updatedProducts = items.map { item ->
                val product = idToProduct[item.productId]
                    ?: throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "상품이 존재하지 않습니다. id=${item.productId}")
                updateStock(product, item.qty)
                product
            }

            productRepository.saveAll(updatedProducts)
        } catch (ex: Exception) {
            eventPublisher.publish(StockFailedEvent(orderUUId, ex))
            log.info("publish StockFailedEvent orderUUId: ${orderUUId} error: ${ex.message}")
            throw ex
        }
    }

    fun updateStock(product: ProductEntity, qty: Int) {
        validateStock(product.stock, qty)
        product.updateStock(qty)
    }

    fun validateStock(stock: Int, qty: Int) {
        if (stock < qty) {
            throw CoreException(ErrorType.OUT_OF_STOCK)
        }
    }
}
