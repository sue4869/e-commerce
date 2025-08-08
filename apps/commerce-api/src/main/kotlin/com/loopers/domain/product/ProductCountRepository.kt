package com.loopers.domain.product

interface ProductCountRepository {

    fun save(productCount: ProductCountEntity): ProductCountEntity

    fun saveAll(productCounts: List<ProductCountEntity>): List<ProductCountEntity>

    fun getByProductId(productId: Long): ProductCountEntity
}
