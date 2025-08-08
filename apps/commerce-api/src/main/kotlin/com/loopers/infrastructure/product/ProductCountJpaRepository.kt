package com.loopers.infrastructure.product


import com.loopers.domain.product.ProductCountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductCountJpaRepository : JpaRepository<ProductCountEntity, Long> {

    fun findByProductId(id: Long): ProductCountEntity?
}
