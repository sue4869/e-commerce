package com.loopers.domain.order

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional(readOnly = true)
@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {

    @Transactional
    fun create(command: OrderCommand.Create): Long {
        val totalPrice = command.items
            .map { it.price.multiply(BigDecimal.valueOf(it.qty.toLong())) }
            .reduce { totalValue, price -> totalValue + price }
        return orderRepository.save(OrderEntity.of(command.userId, totalPrice)).id
    }
}
