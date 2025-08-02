package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.type.OrderItemStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_item")
class OrderItemEntity(
    orderId: Long,
    productHistoryId: Long,
    unitPrice: BigDecimal,
    totalPrice: BigDecimal,
    qty: Int,
    status: OrderItemStatus = OrderItemStatus.ORDERED
): BaseEntity() {

    @Column(name = "order_id")
    val orderId: Long = orderId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    var order: OrderEntity? = null

    @Column(name = "product_history_id")
    val productHistoryId: Long = productHistoryId

    @Column(name = "unit_price")
    val unitPrice: BigDecimal = unitPrice

    @Column(name = "total_price")
    val totalPrice: BigDecimal = totalPrice

    @Column(name = "qty")
    val qty: Int = qty

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: OrderItemStatus = status

    companion object {
        fun of(command: OrderCommand.Item, orderId: Long, productHistoryId: Long): OrderItemEntity {
            return OrderItemEntity(
                orderId = orderId,
                productHistoryId = productHistoryId,
                unitPrice = command.price,
                totalPrice = command.price.multiply(BigDecimal.valueOf(command.qty.toLong())),
                qty = command.qty
            )
        }
    }
}
