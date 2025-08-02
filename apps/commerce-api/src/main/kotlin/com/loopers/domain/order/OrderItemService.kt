package com.loopers.domain.order

import com.loopers.domain.product.ProductHistoryDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Transactional(readOnly = true)
@Service
class OrderItemService(
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    fun create(commands: List<OrderCommand.Item>, orderId: Long, products: List<ProductHistoryDto>): List<OrderItemDto> {
        val productIdToDto = products.associateBy { it.productId }

        val orderItems = commands.map { command ->
            val product = productIdToDto[command.productId]!!
            OrderItemEntity.of(command, orderId, product.productHistoryId)
        }
        orderItemRepository.saveAll(orderItems)
        return orderItems.map { OrderItemDto.of(it) }
    }
}
