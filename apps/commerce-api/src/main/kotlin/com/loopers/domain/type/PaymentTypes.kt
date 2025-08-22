package com.loopers.domain.type

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}

enum class PaymentType(
    val description: String,
) {
    POINT("포인트 결제"),
    CARD("카드결제")
}
