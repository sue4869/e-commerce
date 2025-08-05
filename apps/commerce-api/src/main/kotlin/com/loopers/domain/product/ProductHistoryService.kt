package com.loopers.domain.product

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductHistoryService(
    private val productHistoryRepository: ProductHistoryRepository
) {

    fun getProductsForOrder(productIds: List<Long>): List<ProductHistoryDto> {
        val productHistory = productHistoryRepository.findProductIdToHistoryByProductIds(productIds)
        validateUnExistedProduct(productIds, productHistory.map { it.productId })
        return productHistory.map { ProductHistoryDto.of(it) }
    }

    fun validateUnExistedProduct(reqProductIds: Collection<Long>, findProductIds: Collection<Long>) {
        val foundIds = findProductIds
        val missingIds = reqProductIds.filterNot { it in foundIds }

        if (missingIds.isNotEmpty()) {
            throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "존재하지 않는 상품 ID: $missingIds")
        }
    }
}
