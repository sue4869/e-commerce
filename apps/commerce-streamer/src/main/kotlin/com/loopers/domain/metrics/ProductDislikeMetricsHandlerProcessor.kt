package com.loopers.domain.metrics

import com.loopers.domain.EventType

class ProductDislikeMetricsHandlerProcessor(
    private val productMetricsRepository: ProductMetricsRepository
): IMetricHandlerProcessor {
    override fun supportType(): EventType = EventType.PRODUCT_UNLIKED

    override fun process(productId: Long) {
        val productMetric = productMetricsRepository.findByProductId(productId) ?: ProductMetricsEntity.of(productId)
        productMetric.decreaseLikeCount()
        productMetricsRepository.save(productMetric)
    }
}
