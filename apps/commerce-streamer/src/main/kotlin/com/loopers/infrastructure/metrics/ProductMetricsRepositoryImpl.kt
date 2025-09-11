package com.loopers.infrastructure.metrics

import com.loopers.domain.metrics.ProductMetricsEntity
import com.loopers.domain.metrics.ProductMetricsRepository
import org.springframework.stereotype.Component

@Component
class ProductMetricsRepositoryImpl(
    private val productMetricsJpaRepository: ProductMetricsJpaRepository
): ProductMetricsRepository {

    override fun findByProductId(productId: Long): ProductMetricsEntity? {
        return productMetricsJpaRepository.findByProductId(productId)
    }

    override fun save(entity: ProductMetricsEntity) {
        productMetricsJpaRepository.save(entity)
    }
}
