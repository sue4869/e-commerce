package com.loopers.domain.order

import com.loopers.domain.type.OrderStatus
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.PriceUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {

    @Transactional
    fun create(command: OrderCommand.Create): OrderDto {
        val totalPrice = PriceUtils.getTotalPrice(command.items)
        val order = orderRepository.save(OrderEntity.of(command.userId, totalPrice, command.couponId))
        return OrderDto.of(order)
    }

    @Transactional
    fun updateStatus(orderUUID: String, status: OrderStatus): OrderDto {
        val order = orderRepository.findByUuid(orderUUID)
            ?: throw CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다. orderUUID: $orderUUID")
        order.updateStatus(status)
        orderRepository.save(order)
        return OrderDto.of(order)
    }
}
