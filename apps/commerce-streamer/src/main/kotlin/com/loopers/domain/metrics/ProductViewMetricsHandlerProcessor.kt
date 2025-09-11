package com.loopers.domain.metrics

import com.loopers.domain.EventType

class ProductViewMetricsHandlerProcessor(
    private val productMetricsRepository: ProductMetricsRepository
): IMetricHandlerProcessor {
    override fun supportType() = EventType.PRODUCT_VIEW

    override fun process(productId: Long) {
        val productMetric = productMetricsRepository.findByProductId(productId) ?: ProductMetricsEntity.of(productId)
        productMetric.increaseViewCount()
        productMetricsRepository.save(productMetric)
    }
}
