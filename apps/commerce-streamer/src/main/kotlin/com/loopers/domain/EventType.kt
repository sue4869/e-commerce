package com.loopers.domain

enum class EventType(
    val topic: String,
) {
    ORDER_PAID_COMPLETED(Topic.ORDER_PAID_COMPLETED),
    ORDER_PAID_FAILED(Topic.ORDER_PAID_FAILED),
    PRODUCT_VIEW(Topic.PRODUCT_VIEW),
    PRODUCT_LIKED(Topic.PRODUCT_LIKE_CHANGED),
    PRODUCT_UNLIKED(Topic.PRODUCT_LIKE_CHANGED),
    STOCK_CHANGE_FAILED(Topic.STOCK_CHANGED),
    STOCK_CHANGED(Topic.STOCK_CHANGED),
    STOCK_SOLD_OUT(Topic.STOCK_CHANGED),
    ORDERED(Topic.ORDERED),
    ;

    class Topic {
        companion object {
            const val ORDER_PAID_COMPLETED = "order.v1.paid-completed"
            const val ORDER_PAID_FAILED = "order.v1.paid-failed"
            const val PRODUCT_LIKE_CHANGED = "product.v1.like-changed"
            const val PRODUCT_VIEW = "product.v1.view"
            const val STOCK_CHANGED = "product.v1.stock-changed"
            const val ORDERED = "order.v1.ordered"
        }
    }

    class Group {
        companion object {
            const val METRICS_EVENTS = "metrics-events-consumer"
            const val AUDIT_LOG_EVENTS = "audit-log-events-consumer"
        }
    }
}
