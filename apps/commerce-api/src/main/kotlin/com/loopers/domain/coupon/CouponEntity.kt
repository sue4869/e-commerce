package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.domain.type.DiscountType
import com.loopers.domain.type.DiscountType.*
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update coupon set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "coupon")
class CouponEntity(
    name: String,
    discountType: DiscountType,
    discountValue: Long,
    totalCount: Long,
    issuedCount: Long,
): BaseEntity() {

    @Column(name = "name")
    val name: String = name
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type")
    val discountType: DiscountType = discountType
    @Column(name = "discount_value")
    val discountValue: Long = discountValue
    @Column(name = "total_count")
    val totalCount: Long = totalCount
    @Column(name = "issued_count")
    val issuedCount: Long = issuedCount

    fun getDiscountResult(orderAmount: Long): Long {
        return when (discountType) {
            FIXED -> orderAmount - discountValue
            PERCENTAGE -> orderAmount - (orderAmount * discountValue / 100)
        }.coerceAtLeast(0)
    }
}
