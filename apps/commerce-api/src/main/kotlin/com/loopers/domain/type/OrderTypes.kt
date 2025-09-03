package com.loopers.domain.type

enum class OrderStatus(
    val description: String,
) {
    ORDERED("주문 생성"),
    PAYMENT_PENDING("결제 요청"),
    PAID("결제 완료"),
    CANCELLED("주문 취소"),
    STOCK_FAILED("재고 문제 발생"),
    FAILED("주문 실패"),
}

enum class OrderItemStatus(
    val description: String,
) {
    ORDERED("상품 주문"),
    CANCELLED("상품 취소"),
    FAILED("주문 실패"),
}

enum class CardType {
    SAMSUNG,
    KB,
    HYUNDAI,
}
