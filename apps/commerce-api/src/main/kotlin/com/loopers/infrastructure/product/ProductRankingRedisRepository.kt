package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductRankingRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductRankingRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) : ProductRankingRepository {

    companion object {
        private const val PRODUCT_RANKING_KEY_PREFIX = "rank:all:"
    }

    private fun generateKey(date: LocalDate): String =
        PRODUCT_RANKING_KEY_PREFIX + date.toString()

    override fun getRankingList(date: LocalDate, pageable: Pageable): List<Pair<Long, Double>> {
        val key = generateKey(date)

        val start = (pageable.pageNumber * pageable.pageSize).toLong()
        val end = (start + pageable.pageSize - 1).toLong()

        val tuples = redisTemplate.opsForZSet()
            .reverseRangeWithScores(key, start, end) ?: return emptyList()

        return tuples.mapNotNull { tuple ->
            val productId = (tuple.value as? Long) ?: return@mapNotNull null
            val score = tuple.score ?: 0.0
            productId to score
        }
    }

    override fun getTotalCount(date: LocalDate): Long {
        val key = generateKey(date)
        return redisTemplate.opsForZSet().zCard(key) ?: 0L
    }

    override fun getRank(productId: Long, date: LocalDate): Long? {
        val key = generateKey(date)
        return redisTemplate.opsForZSet().reverseRank(key, productId)?.plus(1) // 0-based index → 1-based rank
    }
}
