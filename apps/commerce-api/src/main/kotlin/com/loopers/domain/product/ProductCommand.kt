package com.loopers.domain.product

import org.springframework.data.domain.Pageable

class ProductCommand {

    data class QueryCriteria(
        val brandIds: List<Long>,
        val productId: Long?,
        val pageable: Pageable,
    )

    data class Like(
        val productId: Long,
        val userId: String,
    )
}
