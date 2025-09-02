package com.loopers.domain.order

interface OrderItemRepository {

    fun saveAll(orderItemEntities: List<OrderItemEntity>)

    fun findByOrderId(orderId: Long): List<OrderItemEntity>

    fun findByOrderUUId(orderUUId: String): List<OrderItemEntity>

}
