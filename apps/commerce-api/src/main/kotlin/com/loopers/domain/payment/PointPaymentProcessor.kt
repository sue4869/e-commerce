package com.loopers.domain.payment

import com.loopers.domain.point.PointEntity
import com.loopers.domain.point.PointRepository
import com.loopers.domain.type.PaymentType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class PointPaymentProcessor(
    private val pointRepository: PointRepository
): IPaymentProcessor {

    override fun supportType(): PaymentType = PaymentType.POINT

    @Transactional
    override fun charge(
        userId: String,
        amount: BigDecimal,
    ) {
        val point = pointRepository.findByUserId(userId) ?: throw CoreException(ErrorType.NOT_FOUND_USER_ID,"포인트가 없는 사용자 입니다. 사용자 ID: ${userId}")
        validatePoint(point.amount, amount)
        updatePoint(point, amount)
    }

    fun updatePoint(point: PointEntity, totalPrice: BigDecimal) {
        point.use(totalPrice)
        pointRepository.save(point)
    }

    fun validatePoint(point: BigDecimal, totalPrice: BigDecimal) {
        if(totalPrice == BigDecimal.ZERO) return

        if(point < totalPrice) {
            throw CoreException(ErrorType.NOT_ENOUGH_POINTS, "포인트가 부족합니다. 결제액 : $totalPrice, 포인트 : $point")
        }
    }
}
