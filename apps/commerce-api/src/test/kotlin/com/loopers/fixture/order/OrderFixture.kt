package com.loopers.fixture.order

import com.loopers.domain.type.PaymentType
import com.loopers.interfaces.api.order.OrderV1Models
import java.math.BigDecimal

sealed class OrderFixture {

    abstract fun create(): OrderV1Models.Request.Create

    object Normal : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = listOf(
                    OrderV1Models.Request.Item(
                        productId = 1L,
                        price = BigDecimal.valueOf(1000),
                        qty = 2
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = BigDecimal.valueOf(1000),
                    )
                )
            )
        }
    }

    object MultipleItems : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = listOf(
                    OrderV1Models.Request.Item(
                        productId = 1L,
                        price = BigDecimal.valueOf(1000),
                        qty = 1
                    ),
                    OrderV1Models.Request.Item(
                        productId = 2L,
                        price = BigDecimal.valueOf(2000),
                        qty = 3
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = BigDecimal.valueOf(1000),
                    )
                )
            )
        }
    }

    object InvalidPrice : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = listOf(
                    OrderV1Models.Request.Item(
                        productId = 1L,
                        price = BigDecimal.valueOf(-100),
                        qty = 1
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = BigDecimal.valueOf(1000),
                    )
                )
            )
        }
    }

    object InvalidQty : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = listOf(
                    OrderV1Models.Request.Item(
                        productId = 1L,
                        price = BigDecimal.valueOf(100),
                        qty = -1
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = BigDecimal.valueOf(1000),
                    )
                )
            )
        }
    }

    object EmptyItems : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = emptyList(),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = BigDecimal.valueOf(1000),
                    )
                )
            )
        }
    }
}
