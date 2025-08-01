package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProductJpaRepository : JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = ["brand"])
    fun findWithBrandById(id: Long): ProductEntity?

    fun findByIdIn(ids: Collection<Long>): List<ProductEntity>

    @Modifying
    @Query("UPDATE ProductEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :productId")
    fun incrementLikeCount(productId: Long): Int

    @Modifying
    @Query("UPDATE ProductEntity p SET p.likeCount = p.likeCount - 1 WHERE p.id = :productId")
    fun decrementLikeCount(productId: Long): Int
}
