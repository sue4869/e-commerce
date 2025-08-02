package com.loopers.domain.type

enum class OrderStatus(
    val description: String,
) {
    ORDERED("주문 생성"),
    PAID("결제 완료"),
    CANCELLED("주문 취소"),
    FAILED("주문 실패")
}

enum class OrderItemStatus(
    val description: String,
) {
    ORDERED("상품 주문"),
    CANCELLED("상품 취소"),
    FAILED("주문 실패")
}

enum class PaymentType(
    val description: String,
) {
    POINT("포인트 결제"),
    CARD("카드결제")
}
