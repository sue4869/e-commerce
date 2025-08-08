package com.loopers.domain.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductCountService(
    private val productCountRepository: ProductCountRepository,
) {

    fun getByProductId(productId: Long): ProductCountDto {
        val productCount = productCountRepository.getByProductId(productId)
        return ProductCountDto(productCount.productId, productCount.likeCount)
    }

    @Transactional
    fun like(productId: Long) {
        val productCount = productCountRepository.findByProductIdWithPessimisticLock(productId) ?: ProductCountEntity(productId)
        productCount.increaseLike(1)
        productCountRepository.save(productCount)
    }

    @Transactional
    fun dislike(productId: Long) {
        val productCount = productCountRepository.findByProductIdWithPessimisticLock(productId) ?: return
        productCount.decreaseLike(1)
        productCountRepository.save(productCount)
    }
}
