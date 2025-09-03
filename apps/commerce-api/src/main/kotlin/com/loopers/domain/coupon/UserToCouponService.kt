package com.loopers.domain.coupon

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.type.IssuedStatus
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserToCouponService(
    private val userToCouponRepository: UserToCouponRepository,
) {

    fun validate(userId: String, couponId: Long, paymentCommand: PaymentCommand.Create) {
        val userToCoupon = userToCouponRepository.findWithCouponByUserIdAndCouponId(userId, couponId)
            ?: throw CoreException(ErrorType.INVALID_COUPON)

        if (userToCoupon.issuedStatus == IssuedStatus.USED) {
            throw CoreException(ErrorType.USED_COUPON)
        }

        val calculateAmount = userToCoupon.coupon!!.getDiscountResult(paymentCommand.originAmount)
        if (paymentCommand.finalAmount != calculateAmount) {
            throw CoreException(ErrorType.INVALID_DISCOUNT)
        }
    }

    @Transactional
    fun updateStatus(userId: String, couponId: Long, status: IssuedStatus) {
        val userToCoupon = userToCouponRepository.findWithCouponByUserIdAndCouponId(userId, couponId)
            ?: throw CoreException(ErrorType.INVALID_COUPON)
        userToCoupon.use()
        userToCouponRepository.save(userToCoupon)
    }
}
