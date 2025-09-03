package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.order.QOrderEntity
import com.loopers.domain.order.QOrderItemEntity
import com.loopers.support.querydsl.CmsQuerydslRepositorySupport

interface OrderItemRepositoryCustom {
    fun findByOrderUUId(orderUUId: String): List<OrderItemEntity>
}

class OrderItemRepositoryCustomImpl : CmsQuerydslRepositorySupport(OrderItemEntity::class.java), OrderItemRepositoryCustom {

    companion object {
        private val orderItem = QOrderItemEntity.orderItemEntity
        private val order = QOrderEntity.orderEntity
    }

    override fun findByOrderUUId(orderUUId: String): List<OrderItemEntity> {
        val result = from(orderItem)
            .innerJoin(order).on(orderItem.orderId.eq(order.id))
            .where(
                order.uuid.eq(orderUUId),
            ).fetch()

        return result
    }
}
