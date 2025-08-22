package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.OrderRepository
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository
): OrderRepository {

    override fun save(order: OrderEntity): OrderEntity {
        return orderJpaRepository.save(order)
    }

    override fun findByUuid(uuid: String): OrderEntity? {
        return orderJpaRepository.findByUuid(uuid)
    }


}
