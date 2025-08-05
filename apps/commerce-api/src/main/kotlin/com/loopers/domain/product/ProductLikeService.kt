package com.loopers.domain.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
) {

    @Transactional
    fun create(command: ProductCommand.Like): Boolean {
        productLikeRepository.findByUserIdAndProductId(command.userId, command.productId)?.let { return false }
        ProductLikeEntity.of(command).apply { productLikeRepository.save(this) }.let { return true }
    }

    @Transactional
    fun delete(command: ProductCommand.Like): Boolean {
        val productLike = productLikeRepository.findByUserIdAndProductId(command.userId, command.productId)
            ?: return false
        productLikeRepository.delete(productLike)
        return true
    }

    fun getMyLikes(userId: String): List<ProductLikeDto> {
        return productLikeRepository.findByUserId(userId).map { ProductLikeDto(it.productId, it.userId) }
    }
}
