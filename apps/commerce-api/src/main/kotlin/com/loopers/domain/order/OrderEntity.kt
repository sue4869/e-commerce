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
import com.github.f4b6a3.uuid.UuidCreator

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update shop_order set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "shop_order")
class OrderEntity(
    uuid: String,
    userId: String,
    couponId: Long? = null,
    totalPrice: Long,
    status: OrderStatus = OrderStatus.ORDERED,
    canceledPrice: Long? = null,
    submittedPrice: Long,
): BaseEntity() {

    @Column(name = "uuid")
    val uuid: String = uuid

    @Column(name = "user_id")
    val userId: String = userId

    @Column(name = "coupon_id")
    val couponId: Long? = couponId

    @Column(name = "total_price")
    val totalPrice: Long = totalPrice

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: OrderStatus = status

    @Column(name = "canceled_price")
    val canceledPrice: Long? = canceledPrice

    @Column(name = "submitted_price")
    val submittedPrice: Long? = submittedPrice

    companion object {
        fun of(userId: String, totalPrice: Long, couponId: Long? = null) = OrderEntity(
            uuid = UuidCreator.getTimeOrderedEpoch().toString(),
            userId = userId,
            couponId = couponId,
            totalPrice = totalPrice,
            submittedPrice = totalPrice
        )
    }

    fun updateStatus(status: OrderStatus) {
        this.status = status
    }
}
