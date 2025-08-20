package com.loopers.domain.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductToUserLikeService(
    private val productToUserLikeRepository: ProductToUserLikeRepository,
) {

    @Transactional
    fun create(command: ProductCommand.Like): Boolean {
        productToUserLikeRepository.findByUserIdAndProductId(command.userId, command.productId)?.let { return false }
        ProductToUserLikeEntity.of(command).apply { productToUserLikeRepository.save(this) }.let { return true }
    }

    @Transactional
    fun delete(command: ProductCommand.Like): Boolean {
        val productLike = productToUserLikeRepository.findByUserIdAndProductId(command.userId, command.productId)
            ?: return false
        productToUserLikeRepository.delete(productLike)
        return true
    }

    fun getMyLikes(userId: String): List<ProductToUserLikeDto> {
        return productToUserLikeRepository.findByUserId(userId).map { ProductToUserLikeDto(it.productId, it.userId) }
    }
}
