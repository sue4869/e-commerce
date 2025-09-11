package com.loopers.domain.metrics

import com.loopers.domain.EventType

class OrderCountMetricsHandlerProcessor(
    private val productMetricsRepository: ProductMetricsRepository
): IMetricHandlerProcessor {
    override fun supportType() = EventType.ORDERED

    override fun process(productId: Long) {
        val productMetric = productMetricsRepository.findByProductId(productId) ?: ProductMetricsEntity.of(productId)
        productMetric.increaseOrderCount()
        productMetricsRepository.save(productMetric)
    }
}
