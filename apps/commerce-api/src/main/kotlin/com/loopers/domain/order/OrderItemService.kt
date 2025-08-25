package com.loopers.domain.order

import com.loopers.domain.product.ProductHistoryDto
import com.loopers.domain.type.OrderItemStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
@Transactional(readOnly = true)
@Service
class OrderItemService(
    private val orderItemRepository: OrderItemRepository
) {

    @Transactional
    fun create(commands: List<OrderCommand.Item>, orderId: Long): List<OrderItemDto> {
        val orderItems = commands.map { OrderItemEntity.of(it, orderId) }
        orderItemRepository.saveAll(orderItems)
        return orderItems.map { OrderItemDto.of(it) }
    }
}
