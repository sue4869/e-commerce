package com.loopers.domain.payment

import com.loopers.domain.type.CardType
import com.loopers.domain.type.PaymentType
import com.loopers.interfaces.api.order.OrderV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class PaymentCommand {

    data class Create(
        val payments: List<Payment>,
        val finalAmount: Long,
        val originAmount: Long,
    ) {
        init {
            val calculatedAmount = payments.sumOf { it.amount }
            require(calculatedAmount == originAmount) { "본래 금액이 잘못되었습니다." }
        }
    }

    data class Payment(
        val type: PaymentType,
        val cardType: CardType? = null,
        val cardNo: String? = null,
        val amount: Long,
    ) {
        private val regexCardNo = Regex("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$")

        init {
            require(amount > 0L) { throw CoreException(ErrorType.INVALID_PAYMENT_PRICE) }
            if (type == PaymentType.CARD) {
                require(cardType != null) { "cardType must not be null" }
                require(!cardNo.isNullOrBlank()) { "cardNo must not be null or blank" }
                require(!regexCardNo.matches(cardNo)) { "카드 번호는 xxxx-xxxx-xxxx-xxxx 형식이어야 합니다" }
            }
        }

        companion object {
            fun of(request: OrderV1Models.Request.Payment) = Payment(
                type = request.type,
                cardType = request.cardType,
                cardNo = request.cardNo,
                amount = request.amount,
            )
        }
    }
}
