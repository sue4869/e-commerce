package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductToUserLikeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductToUserLikeJpaRepository : JpaRepository<ProductToUserLikeEntity, Long> {

    fun findByUserIdAndProductId(userId: String, productId: Long): ProductToUserLikeEntity?

    fun findByUserId(userId: String): List<ProductToUserLikeEntity>
}
