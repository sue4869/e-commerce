package com.loopers.domain.product

import com.loopers.infrastructure.product.ProductRankingRedisRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Transactional(readOnly = true)
@Service
class ProductService(
    val productRepository: ProductRepository,
    val productRankingRedisRepository: ProductRankingRedisRepository
) {

    fun getList(command: ProductCommand.QueryCriteria): Page<ProductListGetDto> {
        return productRepository.findListByCriteria(command)
    }

    fun getWithBrand(productId: Long): ProductWithBrandDto {
        val product = productRepository.getWithBrandById(productId)
        val rank = productRankingRedisRepository.getRank(productId, LocalDate.now())
        return ProductWithBrandDto.of(product, rank)
    }
}
