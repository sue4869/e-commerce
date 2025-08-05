package com.loopers.domain.product

interface ProductHistoryRepository {

    fun saveAll(productHistory: List<ProductHistoryEntity>): List<ProductHistoryEntity>

    fun findProductIdToHistoryByProductIds(productIds: Collection<Long>): List<ProductHistoryEntity>
}
