package com.loopers.interfaces.api.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.type.PaymentType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class OrderV1Models {

    class Request {

        data class Create(
            @field:NotBlank
            val items: List<Item>,
            @field:NotNull
            val paymentType: PaymentType
        ) {
            fun toCommand(userId: String): OrderCommand.Create {
                return OrderCommand.Create(
                    userId = userId,
                    items = items.map { OrderCommand.Item.of(it) },
                    paymentType = paymentType
                )
            }
        }

        data class Item(
            val productId: Long,
            val price: BigDecimal,
            val qty: Int,
        )
    }
}
