package com.loopers.interfaces.api.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.type.CardType
import com.loopers.domain.type.PaymentType
import jakarta.validation.constraints.NotBlank

class OrderV1Models {

    class Request {

        data class Create(
            @field:NotBlank
            val items: List<Item>,
            @field:NotBlank
            val payments: List<Payment>
        ) {
            fun toOrderCommand(userId: String): OrderCommand.Create {
                return OrderCommand.Create(
                    userId = userId,
                    items = items.map { OrderCommand.Item.of(it) },
                )
            }

            fun toPaymentCommand(): PaymentCommand.Create {
                return PaymentCommand.Create(
                    payments = payments.map { PaymentCommand.Payment.of(it) },
                )
            }
        }

        data class Payment(
            val type: PaymentType,
            val cardType: CardType? = null,
            val cardNo: String? = null,
            val amount: Long,
        )

        data class Item(
            val productId: Long,
            val price: Long,
            val qty: Int,
        )
    }
}
