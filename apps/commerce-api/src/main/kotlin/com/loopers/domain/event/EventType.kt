package com.loopers.domain.event

import com.loopers.domain.dto.OrderEvent
import com.loopers.domain.dto.PaidCompletedEvent
import com.loopers.domain.dto.PaidFailedEvent
import com.loopers.domain.dto.ProductDislikedEvent
import com.loopers.domain.dto.ProductLikedEvent
import com.loopers.domain.dto.ProductViewEvent
import com.loopers.domain.dto.StockChangedEvent
import com.loopers.domain.dto.StockFailedEvent

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String,
) {
    ORDER_PAID_COMPLETED(PaidCompletedEvent::class.java, Topic.ORDER_PAID_COMPLETED),
    ORDER_PAID_FAILED(PaidFailedEvent::class.java, Topic.ORDER_PAID_FAILED),
    PRODUCT_VIEW(ProductViewEvent::class.java, Topic.PRODUCT_VIEW),
    PRODUCT_LIKED(ProductLikedEvent::class.java, Topic.PRODUCT_LIKE_CHANGED),
    PRODUCT_UNLIKED(ProductDislikedEvent::class.java, Topic.PRODUCT_LIKE_CHANGED),
    STOCK_CHANGE_FAILED(StockFailedEvent::class.java, Topic.STOCK_CHANGED),
    STOCK_CHANGED(StockChangedEvent::class.java, Topic.STOCK_CHANGED),
    STOCK_SOLD_OUT(StockChangedEvent::class.java, Topic.STOCK_CHANGED),
    ORDERED(OrderEvent::class.java, Topic.ORDERED),
    ;

    class Topic {
        companion object {
            const val ORDER_PAID_COMPLETED = "order.v1.paid-completed"
            const val ORDER_PAID_FAILED = "order.v1.paid-failed"
            const val PRODUCT_LIKE_CHANGED = "product.v1.like-changed"
            const val STOCK_CHANGED = "product.v1.stock-changed"
            const val PRODUCT_VIEW = "product.v1.stock-changed"
            const val ORDERED = "order.v1.ordered"
        }
    }
}
