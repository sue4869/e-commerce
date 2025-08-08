package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.type.OrderStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.math.BigDecimal

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update shop_order set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "shop_order")
class OrderEntity(
    userId: String,
    totalPrice: BigDecimal,
    status: OrderStatus = OrderStatus.ORDERED,
    canceledPrice: BigDecimal? = null,
    submittedPrice: BigDecimal,
): BaseEntity() {

    @Column(name = "userId")
    val userId: String = userId

    @Column(name = "total_price")
    val totalPrice: BigDecimal = totalPrice

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: OrderStatus = status

    @Column(name = "canceled_price")
    val canceledPrice: BigDecimal? = canceledPrice

    @Column(name = "submitted_price")
    val submittedPrice: BigDecimal? = submittedPrice

    companion object {
        fun of(userId: String, totalPrice: BigDecimal) = OrderEntity(
            userId = userId,
            totalPrice = totalPrice,
            submittedPrice = totalPrice
        )
    }
}
