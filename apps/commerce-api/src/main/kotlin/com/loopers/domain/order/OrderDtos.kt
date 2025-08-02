package com.loopers.domain.order

import com.loopers.domain.type.OrderItemStatus
import com.loopers.domain.type.OrderStatus
import java.math.BigDecimal

data class OrderDto(
    val totalPrice: BigDecimal,
    val status: OrderStatus,
    val canceledPrice: BigDecimal,
    val submittedPrice: BigDecimal,
    val items: List<OrderItemDto> = emptyList(),
)

data class OrderItemDto(
    val id: Long,
    val orderId: Long,
    val productHistoryId: Long,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val qty: Int,
    val status: OrderItemStatus,
) {
    companion object {
        fun of(source: OrderItemEntity) = OrderItemDto(
            id = source.id,
            orderId = source.orderId,
            productHistoryId = source.productHistoryId,
            unitPrice = source.unitPrice,
            totalPrice = source.totalPrice,
            qty = source.qty,
            status = source.status,
        )
    }
}

interface IErrorIdsDto {
    var ids: MutableList<Long>
}

data class ProductErrorDto(
    override var ids: MutableList<Long> = mutableListOf()
) : IErrorIdsDto

data class StockErrorDto(
    override var ids: MutableList<Long> = mutableListOf()
) : IErrorIdsDto

data class PaymentErrorDto(
    override var ids: MutableList<Long> = mutableListOf()
) : IErrorIdsDto
