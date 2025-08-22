package com.loopers.application.payment

import com.loopers.domain.order.OrderService
import com.loopers.domain.order.StockService
import com.loopers.domain.payment.AfterPgProcessor
import com.loopers.domain.payment.PgAfterCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
open class PaymentFacade(
    private val afterPgProcessor: AfterPgProcessor,
    private val orderService: OrderService,
    private val stockService: StockService
) {

    @Transactional
    open fun executeAfterPg(command: PgAfterCommand) {
        val status = afterPgProcessor.executeAfterPg(command)
        val orderId = orderService.executeAfterPg(status, command)
        stockService.changeStock(status, orderId)
    }
}
