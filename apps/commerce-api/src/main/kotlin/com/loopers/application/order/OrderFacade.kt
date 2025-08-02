package com.loopers.application.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderItemService
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.StockService
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.product.ProductHistoryService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Component
class OrderFacade(
    private val productHistoryService: ProductHistoryService,
    private val orderService: OrderService,
    private val orderItemService: OrderItemService,
    private val stockService: StockService,
    private val paymentService: PaymentService,

    ) {

    @Transactional
    fun create(command: OrderCommand.Create) {
        val products = productHistoryService.getProductsForOrder(command.items.map { it.productId })
        val productIds = products.map { it.productId }

        //주문 생성
        val orderId = orderService.create(command)
        val orderItems = orderItemService.create(command.items, orderId, products)

        //결제
        paymentService.charge(command.userId, command.paymentType, orderItems)

        //재고
        stockService.changeStock(command, productIds)
    }
}
