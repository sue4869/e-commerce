package com.loopers.domain.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class ProductRankService(
    private val productRankingRepository: ProductRankingRepository,
    private val productRepository: ProductRepository
) {
    fun getRankingDaily(command: ProductCommand.RankingDaily): Page<ProductRankDto> {
        val content = getRanking(command)

        val totalElements = productRankingRepository.getTotalCount(command.date)

        return PageImpl(
            content,
            command.pageable,
            totalElements
        )
    }

    fun getRanking(command: ProductCommand.RankingDaily): List<ProductRankDto> {
        val productScores = productRankingRepository.getRankingList(command.date, command.pageable)
        val productIds = productScores.map { it.first }
        val products = productRepository.findByIdIn(productIds).associateBy { it.id }

        return productScores.mapIndexed { index, (productId, score) ->
            val product = products[productId]
            ProductRankDto(
                rank = command.pageable.pageNumber * command.pageable.pageSize + index + 1,
                productId = productId,
                productName = product?.name ?: "UNKNOWN",
                score = score,
            )
        }
    }
}
