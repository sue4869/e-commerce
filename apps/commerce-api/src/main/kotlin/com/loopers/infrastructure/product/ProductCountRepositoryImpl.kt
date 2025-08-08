package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductCountEntity
import com.loopers.domain.product.ProductCountRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class ProductCountRepositoryImpl (
    private val productCountJpaRepository: ProductCountJpaRepository
): ProductCountRepository {

    override fun save(productCount: ProductCountEntity): ProductCountEntity {
        return productCountJpaRepository.save(productCount)
    }

    override fun saveAll(productCounts: List<ProductCountEntity>): List<ProductCountEntity> {
        return productCountJpaRepository.saveAll(productCounts)
    }

    override fun getByProductId(productId: Long): ProductCountEntity {
        return productCountJpaRepository.findByProductId(productId) ?: throw CoreException(ErrorType.NOT_FOUND)
    }

}
