package com.loopers.domain.event.listener

import com.loopers.domain.coupon.UserToCouponService
import com.loopers.domain.event.dto.PaidCompletedEvent
import com.loopers.domain.event.dto.PaidFailedEvent
import com.loopers.domain.event.dto.StockFailedEvent
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.StockService
import com.loopers.domain.type.IssuedStatus
import com.loopers.domain.type.OrderStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Service
class OrderListener(
    private val orderService: OrderService,
    private val stockService: StockService,
    private val userToCouponService: UserToCouponService,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterPaid(event: PaidCompletedEvent) {
        val orderDto = orderService.updateStatus(event.orderUUId, event.status)
        stockService.reduceStock(event.orderUUId, event.status)
        orderDto.couponId?.let { userToCouponService.updateStatus(orderDto.userId, orderDto.couponId, IssuedStatus.USED) }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterFail(event: PaidFailedEvent) {
        orderService.updateStatus(event.orderUUId, event.status)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleStockFailed(event: StockFailedEvent) {
        orderService.updateStatus(event.orderUUId, OrderStatus.STOCK_FAILED)
        //TODO 결제 취소 pg 요청
    }
}
