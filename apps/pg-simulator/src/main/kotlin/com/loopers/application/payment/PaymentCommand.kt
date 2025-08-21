package com.loopers.application.payment

object PaymentCommand {
    data class CreateTransaction(
        val userId: String,
        val orderId: String,
        val cardType: CardType,
        val cardNo: String,
        val amount: Long,
        val callbackUrl: String,
    ) {
        fun validate() {
            if (amount <= 0L) {
                throw CoreException(ErrorType.BAD_REQUEST, "요청 금액은 0 보다 큰 정수여야 합니다.")
            }
        }
    }
}
