package com.loopers.domain.type

enum class DiscountType(
    val description: String,
) {
    FIXED("정액제"),
    PERCENTAGE("퍼센트제")
}

enum class IssuedStatus(
    val description: String
) {
    AVAILABLE("사용전"),
    USED("사용됨")
}
