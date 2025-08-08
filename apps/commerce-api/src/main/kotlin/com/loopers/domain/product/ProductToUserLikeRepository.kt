package com.loopers.domain.product

interface ProductToUserLikeRepository {

    fun findByUserIdAndProductId(userId: String, productId: Long): ProductToUserLikeEntity?
    fun findByUserId(userId: String): List<ProductToUserLikeEntity>
    fun save(entity: ProductToUserLikeEntity)
    fun delete(entity: ProductToUserLikeEntity)
}
