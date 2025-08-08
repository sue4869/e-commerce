package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductToUserLikeEntity
import com.loopers.domain.product.ProductToUserLikeRepository
import org.springframework.stereotype.Component

@Component
class ProductToUserLikeRepositoryImpl (
    private val productToUserLikeJpaRepository: ProductToUserLikeJpaRepository
): ProductToUserLikeRepository {

    override fun findByUserIdAndProductId(
        userId: String,
        productId: Long,
    ): ProductToUserLikeEntity? {
        return productToUserLikeJpaRepository.findByUserIdAndProductId(userId, productId)
    }

    override fun findByUserId(userId: String): List<ProductToUserLikeEntity> {
        return productToUserLikeJpaRepository.findByUserId(userId)
    }

    override fun save(entity: ProductToUserLikeEntity) {
        productToUserLikeJpaRepository.save(entity)
    }

    override fun delete(entity: ProductToUserLikeEntity) {
        productToUserLikeJpaRepository.delete(entity)
    }
}
