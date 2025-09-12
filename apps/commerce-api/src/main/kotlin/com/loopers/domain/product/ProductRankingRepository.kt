package com.loopers.domain.product

import org.springframework.data.domain.Pageable
import java.time.LocalDate

interface ProductRankingRepository {
    fun getRankingList(date: LocalDate, pageable: Pageable): List<Pair<Long, Double>>
    fun getTotalCount(date: LocalDate): Long
    fun getRank(productId: Long, date: LocalDate): Long?
}
