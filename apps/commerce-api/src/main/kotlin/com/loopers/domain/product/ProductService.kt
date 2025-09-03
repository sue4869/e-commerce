package com.loopers.domain.product

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductService(
    val productRepository: ProductRepository,
) {

    fun getList(command: ProductCommand.QueryCriteria): Page<ProductListGetDto> {
        return productRepository.findListByCriteria(command)
    }

    fun getWithBrand(productId: Long): ProductWithBrandDto {
        val product = productRepository.getWithBrandById(productId)
        return ProductWithBrandDto.of(product)
    }
}
