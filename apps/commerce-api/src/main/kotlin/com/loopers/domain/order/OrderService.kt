package com.loopers.domain.order

import com.loopers.domain.payment.PgAfterCommand
import com.loopers.domain.payment.PgClient
import com.loopers.domain.type.OrderItemStatus
import com.loopers.domain.type.OrderStatus
import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentStatus.*
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val orderRepository: OrderRepository
) {

    @Transactional
    fun create(command: OrderCommand.Create): OrderDto {
        val totalPrice = command.items
            .map { it.price * it.qty.toLong() }
            .reduce { totalValue, price -> totalValue + price }
        val order = orderRepository.save(OrderEntity.of(command.userId, totalPrice))
        return OrderDto.of(order)
    }

    @Transactional
    fun updateStatus(orderUUID: String, status: OrderStatus) {
        val order = orderRepository.findByUuid(orderUUID) ?: throw CoreException(ErrorType.NOT_FOUND,"존재하지 않는 주문입니다. orderUUID: $orderUUID")
        order.updateStatus(status)
        orderRepository.save(order)
    }

    @Transactional
    fun executeAfterPg(status: PaymentStatus, command: PgAfterCommand): Long {
        val order = orderRepository.findByUuid(command.orderId) ?: throw CoreException(ErrorType.NOT_FOUND,"존재하지 않는 주문입니다. orderUUID: $command.orderId")
        when (status) {
            PENDING -> null
            SUCCESS -> order.updateStatus(OrderStatus.PAID)
            FAILED -> order.updateStatus(OrderStatus.CANCELLED)
        }
        orderRepository.save(order)
        return order.id
    }
}
