package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.UserToCouponEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserToCouponJpaRepository : JpaRepository<UserToCouponEntity, Long> {

    @EntityGraph(attributePaths = ["coupon"])
    fun findWithCouponByUserIdAndCouponId(userId: String, couponId: Long): UserToCouponEntity?
}
