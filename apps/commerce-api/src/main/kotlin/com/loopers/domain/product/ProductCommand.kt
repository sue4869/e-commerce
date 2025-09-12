package com.loopers.domain.product

import org.springframework.data.domain.Pageable
import java.time.LocalDate

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

    data class RankingDaily(
        val date: LocalDate,
        val pageable: Pageable
    )
}
