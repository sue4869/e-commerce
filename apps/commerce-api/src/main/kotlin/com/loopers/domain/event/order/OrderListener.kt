package com.loopers.domain.event.order

import com.loopers.domain.event.dto.OrderStatusChangeDto
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.StockService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class OrderListener(
    private val orderService: OrderService,
    private val stockService: StockService
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: OrderStatusChangeDto) {
        orderService.updateStatus(event.orderUUId, event.status)
        stockService.reduceStock(event.orderUUId, event.status)
    }
}
