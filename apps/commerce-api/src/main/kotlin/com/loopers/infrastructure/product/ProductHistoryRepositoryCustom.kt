package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductHistoryEntity
import com.loopers.domain.product.QProductHistoryEntity
import com.loopers.support.querydsl.CmsQuerydslRepositorySupport
import com.querydsl.core.group.GroupBy.groupBy

interface ProductHistoryRepositoryCustom {

    fun findProductIdToHistoryByProductIds(productIds: Collection<Long>): List<ProductHistoryEntity>
}

class ProductHistoryRepositoryCustomImpl : CmsQuerydslRepositorySupport(ProductHistoryEntity::class.java), ProductHistoryRepositoryCustom {

    companion object {
        private val productHistory = QProductHistoryEntity.productHistoryEntity
    }

    override fun findProductIdToHistoryByProductIds(productIds: Collection<Long>): List<ProductHistoryEntity> {

        val productIdToLatestId: Map<Long, Long> = from(productHistory)
            .where(productHistory.productId.`in`(productIds))
            .groupBy(productHistory.productId)
            .transform(
                groupBy(productHistory.productId)
                    .`as`(productHistory.id.max())
            )

        val latestHistories: List<ProductHistoryEntity> = from(productHistory)
            .where(productHistory.id.`in`(productIdToLatestId.values))
            .fetch()

        return latestHistories
    }

}
