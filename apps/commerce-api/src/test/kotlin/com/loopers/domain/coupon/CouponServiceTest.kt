package com.loopers.domain.coupon

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.type.DiscountType
import com.loopers.domain.type.IssuedStatus
import com.loopers.domain.type.PaymentType
import com.loopers.support.error.CoreException
import org.junit.jupiter.api.DisplayName
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import kotlin.test.Test
import com.loopers.support.error.ErrorType
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.assertDoesNotThrow

@ExtendWith(MockitoExtension::class)
class CouponServiceTest {

    @Mock
    lateinit var userToCouponRepository: UserToCouponRepository

    @InjectMocks
    lateinit var userToCouponService: UserToCouponService

    private val userId = "user-123"
    private val couponId = 1L

    @Test
    @DisplayName("쿠폰 검증 성공 - 사용 가능하고 할인 금액이 일치하면 예외가 발생하지 않는다")
    fun `쿠폰 검증 성공`() {
        val coupon = CouponEntity("TEST", DiscountType.FIXED, 1000, 10, 0)
        val userToCoupon = UserToCouponEntity(couponId, userId, issuedStatus = IssuedStatus.AVAILABLE).apply {
            this.coupon = coupon
        }

        whenever(userToCouponRepository.findWithCouponByUserIdAndCouponId(userId, couponId))
            .thenReturn(userToCoupon)

        val paymentCommand = PaymentCommand.Create(
            payments = listOf(
                PaymentCommand.Payment(type = PaymentType.POINT, amount = 5000),
            ),
            originAmount = 5000,
            finalAmount = 4000,
        )

        assertDoesNotThrow {
            userToCouponService.validate(userId, couponId, paymentCommand)
        }
    }

    @Test
    @DisplayName("쿠폰이 존재하지 않으면 INVALID_COUPON 예외를 발생시킨다")
    fun `쿠폰 없음`() {
        whenever(userToCouponRepository.findWithCouponByUserIdAndCouponId(userId, couponId))
            .thenReturn(null)

        val paymentCommand = PaymentCommand.Create(
            payments = listOf(
                PaymentCommand.Payment(type = PaymentType.POINT, amount = 5000),
            ),
            originAmount = 5000,
            finalAmount = 4000,
        )

        val ex = assertThrows<CoreException> {
            userToCouponService.validate(userId, couponId, paymentCommand)
        }

        assert(ex.errorType == ErrorType.INVALID_COUPON)
    }

    @Test
    @DisplayName("이미 사용된 쿠폰이면 USED_COUPON 예외를 발생시킨다")
    fun `이미 사용된 쿠폰`() {
        val coupon = CouponEntity("TEST", DiscountType.FIXED, 1000, 10, 0)
        val userToCoupon = UserToCouponEntity(couponId, userId, issuedStatus = IssuedStatus.USED).apply {
            this.coupon = coupon
        }

        whenever(userToCouponRepository.findWithCouponByUserIdAndCouponId(userId, couponId))
            .thenReturn(userToCoupon)

        val paymentCommand = PaymentCommand.Create(
            payments = listOf(
                PaymentCommand.Payment(type = PaymentType.POINT, amount = 5000),
            ),
            originAmount = 5000,
            finalAmount = 4000,
        )

        val ex = assertThrows<CoreException> {
            userToCouponService.validate(userId, couponId, paymentCommand)
        }

        assert(ex.errorType == ErrorType.USED_COUPON)
    }

    @Test
    @DisplayName("할인 금액이 잘못되면 INVALID_DISCOUNT 예외를 발생시킨다")
    fun `할인 금액 불일치`() {
        val coupon = CouponEntity("TEST", DiscountType.FIXED, 1000, 10, 0)
        val userToCoupon = UserToCouponEntity(couponId, userId, issuedStatus = IssuedStatus.AVAILABLE).apply {
            this.coupon = coupon
        }

        whenever(userToCouponRepository.findWithCouponByUserIdAndCouponId(userId, couponId))
            .thenReturn(userToCoupon)

        val paymentCommand = PaymentCommand.Create(
            payments = listOf(
                PaymentCommand.Payment(type = PaymentType.POINT, amount = 5000),
            ),
            originAmount = 5000,
            finalAmount = 4500,
        )

        val ex = assertThrows<CoreException> {
            userToCouponService.validate(userId, couponId, paymentCommand)
        }

        assert(ex.errorType == ErrorType.INVALID_DISCOUNT)
    }
}
