package com.loopers.utils

import com.loopers.domain.order.OrderCommand.Item

object PriceUtils {

    fun getTotalPrice(items: List<Item>): Long {
        return items
            .map { it.price * it.qty.toLong() }
            .reduce { totalValue, price -> totalValue + price }
    }
}
