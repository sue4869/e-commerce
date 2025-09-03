package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.UserToCouponEntity
import com.loopers.domain.coupon.UserToCouponRepository
import org.springframework.stereotype.Component

@Component
class UserToCouponRepositoryImpl(
    private val userToCouponJpaRepository: UserToCouponJpaRepository,
) : UserToCouponRepository {

    override fun save(userToCoupon: UserToCouponEntity): UserToCouponEntity {
        return userToCouponJpaRepository.save(userToCoupon)
    }

    override fun findWithCouponByUserIdAndCouponId(
        userId: String,
        couponId: Long,
    ): UserToCouponEntity? {
        return userToCouponJpaRepository.findWithCouponByUserIdAndCouponId(userId, couponId)
    }
}
