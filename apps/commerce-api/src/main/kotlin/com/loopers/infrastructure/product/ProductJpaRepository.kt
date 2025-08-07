package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param

interface ProductJpaRepository : JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = ["brand"])
    fun findWithBrandById(id: Long): ProductEntity?

    fun findByIdIn(ids: Collection<Long>): List<ProductEntity>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(
        value = [QueryHint(name = "javax.persistence.lock.timeout", value = "3000")]
    )
    @Query("""
    SELECT p 
    FROM ProductEntity p 
    WHERE p.id IN :ids AND p.deletedAt IS NULL
    """)
    fun findByIdInWithPessimisticLock(@Param("ids") ids: Collection<Long>): List<ProductEntity>


    @Modifying
    @Query("UPDATE ProductEntity p SET p.likeCount = p.likeCount + 1 WHERE p.id = :productId")
    fun incrementLikeCount(productId: Long): Int

    @Modifying
    @Query("UPDATE ProductEntity p SET p.likeCount = p.likeCount - 1 WHERE p.id = :productId")
    fun decrementLikeCount(productId: Long): Int
}
