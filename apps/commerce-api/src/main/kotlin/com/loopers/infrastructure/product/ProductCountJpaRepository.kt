package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductCountEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.query.Param

interface ProductCountJpaRepository : JpaRepository<ProductCountEntity, Long> {

    fun findByProductId(id: Long): ProductCountEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(
        value = [QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")],
    )
    @Query("SELECT p FROM ProductCountEntity p WHERE p.productId = :productId AND p.deletedAt IS NULL")
    fun findByProductIdWithPessimisticLock(@Param("productId") productId: Long): ProductCountEntity?
}
