package com.loopers.domain.product

interface ProductLikeRepository {

    fun findByUserIdAndProductId(userId: String, productId: Long): ProductLikeEntity?
    fun findByUserId(userId: String): List<ProductLikeEntity>
    fun save(entity: ProductLikeEntity)
    fun delete(entity: ProductLikeEntity)
}
