package com.loopers.domain.metrics

import com.loopers.domain.EventType

class ProductLikeMetricsHandlerProcessor(
    private val productMetricsRepository: ProductMetricsRepository
): IMetricHandlerProcessor  {
    override fun supportType() = EventType.PRODUCT_LIKED

    override fun process(productId: Long) {
        val productMetric = productMetricsRepository.findByProductId(productId) ?: ProductMetricsEntity.of(productId)
        productMetric.increaseLikeCount()
        productMetricsRepository.save(productMetric)
    }
}
