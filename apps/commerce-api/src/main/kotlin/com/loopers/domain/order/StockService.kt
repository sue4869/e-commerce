package com.loopers.domain.order

import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductHistoryEntity
import com.loopers.domain.product.ProductHistoryRepository
import com.loopers.domain.product.ProductRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class StockService(
    private val productRepository: ProductRepository,
    private val productHistoryRepository: ProductHistoryRepository,
) {

    @Transactional
    fun changeStock(command: OrderCommand.Create, productIds: List<Long>) {
        val idToProduct = productRepository.findByIdIn(productIds).associateBy { it.id }

        val updatedProducts = command.items.map { command ->
            val product = idToProduct[command.productId] ?: throw CoreException(ErrorType.PRODUCT_NOT_FOUND, "상품이 존재하지 않습니다. id=${command.productId}")
            updateStock(product, command.qty)
            product
        }

        val hitories = updatedProducts.map { ProductHistoryEntity.of(it) }
        productRepository.saveAll(updatedProducts)
        productHistoryRepository.saveAll(hitories)
    }

    fun updateStock(product: ProductEntity, qty: Int) {
        validateStock(product.stock, qty)
        product.updateStock(qty)
    }

    fun validateStock(stock: Int, qty: Int) {
        if (stock < qty) {
            throw CoreException(ErrorType.OUT_OF_STOCK)
        }
    }
}
