package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductHistoryJpaRepository : JpaRepository<ProductHistoryEntity, Long>, ProductHistoryRepositoryCustom {

    fun findByProductIdIn(productIds: List<Long>): List<ProductHistoryEntity>
}
