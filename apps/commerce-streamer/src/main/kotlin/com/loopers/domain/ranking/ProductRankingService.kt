package com.loopers.domain.ranking

import com.loopers.domain.EventType
import com.loopers.domain.type.TargetType
import com.loopers.domain.type.WeightType
import com.loopers.interfaces.consumer.MetricKafkaEvent
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate

private val log = KotlinLogging.logger {}

@Component
class ProductRankingService(
    private val rankingWeightRepository: RankingWeightRepository,
    private val productRankingRepository: ProductRankingRepository
) {

    companion object {
        private const val DEFAULT_VIEW_WEIGHT = 0.05
        private const val DEFAULT_PURCHASE_WEIGHT = 1.0
        private const val DEFAULT_LIKE_WEIGHT = 0.25

    }

    fun rank(event: MetricKafkaEvent?) {
        if (event == null) {
            log.error { "ProductRankingService: MetricKafkaEvent is null" }
            return
        }

        when (event.eventType) {
            EventType.PRODUCT_VIEW -> rankByViews(event)
            EventType.PRODUCT_LIKED -> rankByLikes(event)
            EventType.PRODUCT_UNLIKED -> rankByLikes(event)
            EventType.ORDERED -> rankByPurchases(event)
            else -> throw IllegalArgumentException("Unsupported eventType $event.eventType")
        }
    }

    fun rankByViews(event: MetricKafkaEvent) {
        val score = calculateScoreByView()
        productRankingRepository.incrementScore(event.productId!!, score, LocalDate.now())
    }

    private fun calculateScoreByView(views: Long = 1L): Double {
        val weight = getWeight(WeightType.VIEW)
        return views * weight
    }

    fun rankByPurchases(event: MetricKafkaEvent) {
        val productIdScoreMap = event.items!!.associate { item ->
            val score = calculateScoreByPriceAndQty(item.price, item.qty)
            item.productId to score
        }

        productRankingRepository.incrementScoreAllAtOnce(productIdScoreMap, LocalDate.now())
    }

    private fun calculateScoreByPriceAndQty(price: Long, qty: Int): Double {
        val weight = getWeight(WeightType.ORDER)
        return (price * qty) * weight
    }

    fun rankByLikes(event: MetricKafkaEvent) {
        val score = calculateScoreByLike()
        productRankingRepository.incrementScore(event.productId!!, score, LocalDate.now())
    }

    private fun calculateScoreByLike(likes: Long = 1L): Double {
        val weight = getWeight(WeightType.LIKE)
        return likes * weight
    }

    private fun getWeight(weightType: WeightType): Double {
        val rankingWeight = rankingWeightRepository
            .findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(TargetType.PRODUCT, weightType)

        return rankingWeight?.weight ?: when (weightType) {
            WeightType.VIEW -> DEFAULT_VIEW_WEIGHT
            WeightType.ORDER -> DEFAULT_PURCHASE_WEIGHT
            WeightType.LIKE -> DEFAULT_LIKE_WEIGHT
        }
    }
}
