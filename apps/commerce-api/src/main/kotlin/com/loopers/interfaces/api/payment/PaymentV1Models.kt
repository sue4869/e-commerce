package com.loopers.interfaces.api.payment

import com.loopers.domain.payment.PgAfterCommand
import com.loopers.domain.type.CardType
import com.loopers.domain.type.PaymentStatus

data class HandlePaymentAfterRequest(
    val transactionKey: String,
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val status: String,
    val reason: String?,
) {
    fun toCommand(): PgAfterCommand =
        PgAfterCommand(
            transactionKey,
            orderId,
            cardType,
            cardNo,
            amount,
            reason,
        )
}
