package com.loopers.domain.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductCountService(
    private val productCountRepository: ProductCountRepository,
    private val productRepository: ProductRepository,

) {

    fun getByProductId(productId: Long): ProductCountDto {
        val productCount = productCountRepository.getByProductId(productId)
        return ProductCountDto(productCount.productId, productCount.likeCount)
    }

    @Transactional
    fun like(productId: Long) {
        val product = productRepository.findById(productId)
        val productCount = productCountRepository.getByProductId(product.id)
        productCount.increaseLike(1)
        productCountRepository.save(productCount)
    }

    @Transactional
    fun dislike(productId: Long) {
        val product = productRepository.findById(productId)
        val productCount = productCountRepository.getByProductId(product.id)
        productCount.decreaseLike(1)
        productCountRepository.save(productCount)
    }
}
