package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.type.PaymentStatus
import com.loopers.domain.type.PaymentType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "payment")
class PaymentEntity(
    orderUUId: String,
    paymentKey: String? = null,
    amount: Long,
    type: PaymentType,
    status: PaymentStatus,
) : BaseEntity() {

    var orderUUId: String = orderUUId
    var paymentKey: String? = paymentKey
    var amount: Long = amount

    @Enumerated(EnumType.STRING)
    var type: PaymentType = type

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = status

    fun updateStatus(status: PaymentStatus) {
        this.status = status
    }

    companion object {
        fun of(orderUUId: String, payment: PaymentCommand.Payment) =
            PaymentEntity(
                orderUUId = orderUUId,
                amount = payment.amount,
                type = payment.type,
                status = PaymentStatus.PENDING,
            )
    }
}
