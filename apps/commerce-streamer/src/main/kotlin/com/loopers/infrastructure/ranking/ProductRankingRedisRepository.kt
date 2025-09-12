package com.loopers.infrastructure.ranking

import com.loopers.domain.ranking.ProductRankingRepository
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Duration

@Component
class ProductRankingRedisRepository(
    private val redisTemplate: RedisTemplate<String, Any>
) : ProductRankingRepository {

    companion object {
        private const val PRODUCT_RANKING_KEY_PREFIX = "rank:all:"
        private val TTL: Duration = Duration.ofDays(2)
    }

    private fun generateKey(actionedAt: LocalDate): String =
        PRODUCT_RANKING_KEY_PREFIX + actionedAt.toString()

    override fun incrementScore(productId: Long, score: Double, actionedAt: LocalDate) {
        val rankingKey = generateKey(actionedAt)
        redisTemplate.opsForZSet().incrementScore(rankingKey, productId, score)
        ensureExpire(rankingKey)
    }

    override fun incrementScoreAllAtOnce(
        productIdScoreMap: Map<Long, Double>,
        actionedAt: LocalDate
    ) {
        val rankingKey = generateKey(actionedAt)
        redisTemplate.executePipelined { operations ->
            val ops = operations as RedisOperations<String, Any>
            productIdScoreMap.forEach { (productId, score) ->
                ops.opsForZSet().incrementScore(rankingKey, productId, score)
            }
            null
        }
        ensureExpire(rankingKey)
    }

    private fun ensureExpire(key: String) {
        val currentTtl = redisTemplate.getExpire(key)
        if (currentTtl == -1L) {
            redisTemplate.expire(key, TTL)
        }
    }
}
