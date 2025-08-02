package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductHistoryEntity
import com.loopers.domain.product.ProductHistoryRepository
import org.springframework.stereotype.Component

@Component
class ProductHistoryRepositoryImpl(
    private val productHistoryJpaRepository: ProductHistoryJpaRepository
): ProductHistoryRepository {

    override fun saveAll(productHistory: List<ProductHistoryEntity>): List<ProductHistoryEntity> {
        return productHistoryJpaRepository.saveAll(productHistory)
    }

    override fun findProductIdToHistoryByProductIds(productIds: Collection<Long>): List<ProductHistoryEntity> {
        return productHistoryJpaRepository.findProductIdToHistoryByProductIds(productIds)
    }
}
