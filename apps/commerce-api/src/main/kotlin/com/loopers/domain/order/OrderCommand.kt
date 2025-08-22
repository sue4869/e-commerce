package com.loopers.domain.order

import com.loopers.interfaces.api.order.OrderV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class OrderCommand {

    data class Create(
        val userId: String,
        val items: List<Item>
    )

    data class Item(
        val productId: Long,
        val price: Long,
        val qty: Int,
    ) {

        init {
            require(qty > 0) { throw CoreException(ErrorType.QTY_MUST_BE_POSITIVE) }
            require(price > 0L) { throw CoreException(ErrorType.PRICE_MUST_BE_POSITIVE) }
        }

        companion object {
            fun of(request: OrderV1Models.Request.Item) = Item(
                productId = request.productId,
                price = request.price,
                qty = request.qty
            )
        }
    }
}
