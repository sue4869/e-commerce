package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeEntity
import com.loopers.domain.product.ProductLikeRepository
import org.springframework.stereotype.Component

@Component
class ProductLikeRepositoryImpl (
    private val productLikeJpaRepository: ProductLikeJpaRepository
): ProductLikeRepository {

    override fun findByUserIdAndProductId(
        userId: String,
        productId: Long,
    ): ProductLikeEntity? {
        return productLikeJpaRepository.findByUserIdAndProductId(userId, productId)
    }

    override fun findByUserId(userId: String): List<ProductLikeEntity> {
        return productLikeJpaRepository.findByUserId(userId)
    }

    override fun save(entity: ProductLikeEntity) {
        productLikeJpaRepository.save(entity)
    }

    override fun delete(entity: ProductLikeEntity) {
        productLikeJpaRepository.delete(entity)
    }
}
