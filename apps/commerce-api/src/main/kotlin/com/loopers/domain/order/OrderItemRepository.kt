package com.loopers.domain.order

interface OrderItemRepository {

    fun saveAll(orderItemEntities: List<OrderItemEntity>)
}
