package com.loopers.domain.event.dto

import com.loopers.domain.event.EventPayload

class ProductLikeEvent(
    val productId: Long
): EventPayload

class ProductDislikeEvent(
    val productId: Long
): EventPayload
