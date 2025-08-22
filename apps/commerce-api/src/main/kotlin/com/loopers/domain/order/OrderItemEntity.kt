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
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update order_item set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "order_item")
class OrderItemEntity(
    orderId: Long,
    productId: Long,
    unitPrice: Long,
    totalPrice: Long,
    qty: Int,
    status: OrderItemStatus = OrderItemStatus.ORDERED
): BaseEntity() {

    @Column(name = "order_id")
    val orderId: Long = orderId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    var order: OrderEntity? = null

    @Column(name = "product_id")
    val productId: Long = productId

    @Column(name = "unit_price")
    val unitPrice: Long = unitPrice

    @Column(name = "total_price")
    val totalPrice: Long = totalPrice

    @Column(name = "qty")
    val qty: Int = qty

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: OrderItemStatus = status

    companion object {
        fun of(command: OrderCommand.Item, orderId: Long): OrderItemEntity {
            return OrderItemEntity(
                orderId = orderId,
                productId = command.productId,
                unitPrice = command.price,
                totalPrice = command.price * command.qty,
                qty = command.qty
            )
        }
    }
}
