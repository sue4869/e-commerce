package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLikeJpaRepository : JpaRepository<ProductLikeEntity, Long> {

    fun findByUserIdAndProductId(userId: String, productId: Long): ProductLikeEntity?

    fun findByUserId(userId: String): List<ProductLikeEntity>
}
