package com.loopers.fixture.order

import com.loopers.domain.type.PaymentType
import com.loopers.interfaces.api.order.OrderV1Models

sealed class OrderFixture {

    abstract fun create(): OrderV1Models.Request.Create

    object Normal : OrderFixture() {
        override fun create(): OrderV1Models.Request.Create {
            return OrderV1Models.Request.Create(
                items = listOf(
                    OrderV1Models.Request.Item(
                        productId = 1L,
                        price = 1000L,
                        qty = 2
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = 1000L,
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
                        price = 1000L,
                        qty = 1
                    ),
                    OrderV1Models.Request.Item(
                        productId = 2L,
                        price = 2000L,
                        qty = 3
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = 1000L,
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
                        price = -100L,
                        qty = 1
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = 1000L,
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
                        price = 100L,
                        qty = -1
                    )
                ),
                payments = listOf(
                    OrderV1Models.Request.Payment(
                        type = PaymentType.POINT,
                        amount = 1000L,
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
                        amount = 1000L,
                    )
                )
            )
        }
    }
}
