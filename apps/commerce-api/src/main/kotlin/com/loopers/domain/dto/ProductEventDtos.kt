package com.loopers.domain.dto

import com.loopers.domain.event.EventPayload
import com.loopers.domain.event.EventType

class ProductLikedEvent(
    val productId: Long,
) : EventPayload

class ProductDislikedEvent(
    val productId: Long,
) : EventPayload

class ProductViewEvent(
    val productId: Long,
) : EventPayload

class ProductKafkaEvent(
    val productId: Long,
    val event: EventType,
    val message: String,
) : EventPayload
