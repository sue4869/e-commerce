package com.loopers.domain.order

import com.loopers.domain.type.OrderItemStatus
import com.loopers.domain.type.OrderStatus

data class OrderDto(
    val orderId: Long,
    val uuid: String,
    val userId: String,
    val totalPrice: Long,
    val status: OrderStatus,
    val canceledPrice: Long?,
    val submittedPrice: Long?
) {
    companion object {
        fun of(source: OrderEntity) = OrderDto(
            orderId = source.id,
            uuid = source.uuid,
            userId = source.userId,
            totalPrice = source.totalPrice,
            status = source.status,
            canceledPrice = source.canceledPrice,
            submittedPrice = source.submittedPrice
        )
    }
}

data class OrderItemDto(
    val id: Long,
    val orderId: Long,
    val productId: Long,
    val unitPrice: Long,
    val totalPrice: Long,
    val qty: Int,
    val status: OrderItemStatus,
) {
    companion object {
        fun of(source: OrderItemEntity) = OrderItemDto(
            id = source.id,
            orderId = source.orderId,
            productId = source.productId,
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
