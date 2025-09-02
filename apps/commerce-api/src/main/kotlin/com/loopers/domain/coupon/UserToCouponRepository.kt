package com.loopers.domain.coupon

interface UserToCouponRepository {

    fun save(userToCoupon: UserToCouponEntity): UserToCouponEntity

    fun findWithCouponByUserIdAndCouponId(userId: String, couponId: Long): UserToCouponEntity?
}
