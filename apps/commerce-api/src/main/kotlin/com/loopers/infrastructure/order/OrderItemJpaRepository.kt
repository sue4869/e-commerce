package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemJpaRepository: JpaRepository<OrderItemEntity, Long> {
}
