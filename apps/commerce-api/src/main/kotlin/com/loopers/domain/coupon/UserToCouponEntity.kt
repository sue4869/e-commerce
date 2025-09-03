package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.domain.type.IssuedStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.FetchType
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@SQLRestriction("deleted_at is null")
@SQLDelete(sql = "update user_to_coupon set deleted_at = CURRENT_TIMESTAMP where id = ?")
@Entity
@Table(name = "user_to_coupon")
class UserToCouponEntity(
    couponId: Long,
    userId: String,
    issuedStatus: IssuedStatus = IssuedStatus.AVAILABLE,
    issuedAt: LocalDateTime = LocalDateTime.now(),
    usedAt: LocalDateTime? = null,
) : BaseEntity() {
    @Column(name = "coupon_id")
    val couponId: Long = couponId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", insertable = false, updatable = false)
    var coupon: CouponEntity? = null

    @Column(name = "user_id")
    val userId: String = userId

    @Enumerated(EnumType.STRING)
    @Column(name = "issued_status")
    var issuedStatus: IssuedStatus = issuedStatus

    @Column(name = "issued_at")
    val issuedAt: LocalDateTime? = issuedAt

    @Column(name = "used_at")
    var usedAt: LocalDateTime? = usedAt

    fun use() {
        issuedStatus = IssuedStatus.USED
        usedAt = LocalDateTime.now()
    }
}
