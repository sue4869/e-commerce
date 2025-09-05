package com.loopers.infrastructure.metrics

import com.loopers.domain.metrics.ProductMetricsEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductMetricsJpaRepository: JpaRepository<ProductMetricsEntity, Long> {

    fun findByProductId(productId: Long): ProductMetricsEntity?
}
