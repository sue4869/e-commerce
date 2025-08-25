package com.loopers.domain.payment

import com.loopers.domain.type.CardType
import com.loopers.domain.type.PaymentStatus

data class PgRequest(
    val orderId: String,
    val cardType: String,
    val cardNo: String,
    val amount: Long,
    val callbackUrl: String,
)

data class TransactionResponse(
    val transactionId: String,
    val status: String,
    val approvedAt: String?,
)

data class TransactionDetailResponse(
    val transactionKey: String,
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val status: PaymentStatus,
    val reason: String?,
)

data class PgOfOrderResponse(
    val orderId: String,
    val transactions: List<TransactionResponse>,
)


data class PgAfterCommand(
    val transactionKey: String,
    val orderId: String,
    val cardType: CardType,
    val cardNo: String,
    val amount: Long,
    val reason: String?,
)
