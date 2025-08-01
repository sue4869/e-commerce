package com.loopers.domain.payment

import com.loopers.domain.order.OrderItemDto
import com.loopers.domain.type.PaymentType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PaymentService(
    private val pointCharger: PointCharger
) {

    fun charge(userId: String, type: PaymentType, orderItems: List<OrderItemDto>) {

        val payCharger: IPaymentCharger = when (type) {
            PaymentType.POINT -> pointCharger
            PaymentType.CARD -> pointCharger //TODO 추후 pg사 연결
        }
        payCharger.charge(userId, orderItems)
    }
}
