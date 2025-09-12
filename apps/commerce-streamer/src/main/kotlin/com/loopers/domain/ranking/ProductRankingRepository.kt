package com.loopers.domain.ranking

import java.time.LocalDate

interface ProductRankingRepository {

    fun incrementScore(productId: Long, score: Double, actionedAt: LocalDate)

    fun incrementScoreAllAtOnce(
        productIdScoreMap: Map<Long, Double>,
        actionedAt: LocalDate
    )

}
