package com.loopers.domain.metrics

import com.loopers.domain.EventType

interface IMetricHandlerProcessor {
    fun supportType(): EventType
    fun process(productId: Long)

}
