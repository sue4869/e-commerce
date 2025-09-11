package com.loopers.domain.metrics

interface ProductMetricsRepository {

    fun findByProductId(productId: Long): ProductMetricsEntity?

    fun save(entity: ProductMetricsEntity)
}
