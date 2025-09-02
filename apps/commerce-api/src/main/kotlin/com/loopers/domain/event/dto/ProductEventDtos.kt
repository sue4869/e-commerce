package com.loopers.domain.event.dto

import com.loopers.domain.event.EventPayload

class ProductLikedEvent(
    val productId: Long
): EventPayload

class ProductDislikedEvent(
    val productId: Long
): EventPayload
