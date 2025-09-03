package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.order.OrderItemRepository
import org.springframework.stereotype.Component

@Component
class OrderItemRepositoryImpl(
    private val orderItemJpaRepository: OrderItemJpaRepository,
) : OrderItemRepository {

    override fun saveAll(orderItemEntities: List<OrderItemEntity>) {
        orderItemJpaRepository.saveAll(orderItemEntities)
    }

    override fun findByOrderId(orderId: Long): List<OrderItemEntity> {
        return orderItemJpaRepository.findByOrderId(orderId)
    }

    override fun findByOrderUUId(orderUUId: String): List<OrderItemEntity> {
        return orderItemJpaRepository.findByOrderUUId(orderUUId)
    }
}
