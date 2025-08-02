package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductListGetDto
import com.loopers.domain.product.ProductRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class ProductRepositoryImpl (
    private val productJpaRepository: ProductJpaRepository
): ProductRepository {

    override fun save(product: ProductEntity): ProductEntity {
        return productJpaRepository.save(product)
    }

    override fun saveAll(product: List<ProductEntity>): List<ProductEntity> {
        return productJpaRepository.saveAll(product)
    }

    override fun findById(productId: Long): ProductEntity {
        return productJpaRepository.findById(productId)
            .orElseThrow { CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.") }
    }

    override fun findListByCriteria(command: ProductCommand.QueryCriteria): Page<ProductListGetDto> {
        return productJpaRepository.findListByCriteria(command)
    }

    override fun findByIdIn(ids: Collection<Long>): List<ProductEntity> {
        return productJpaRepository.findByIdIn(ids)
    }

    override fun getWithBrandById(id: Long): ProductEntity {
        return productJpaRepository.findWithBrandById(id) ?: throw CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.")
    }

    override fun incrementLikeCount(productId: Long): Int {
        return productJpaRepository.incrementLikeCount(productId)
    }

    override fun decrementLikeCount(productId: Long) {
        productJpaRepository.decrementLikeCount(productId)
    }
}
