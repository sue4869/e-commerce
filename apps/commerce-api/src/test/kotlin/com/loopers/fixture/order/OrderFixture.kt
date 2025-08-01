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
                paymentType = PaymentType.POINT
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
                paymentType = PaymentType.POINT
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
                paymentType = PaymentType.POINT
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
                paymentType = PaymentType.POINT
            )
        }
    }

    object EmptyItems : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = emptyList(),
                paymentType = PaymentType.POINT
            )
        }
    }
}
