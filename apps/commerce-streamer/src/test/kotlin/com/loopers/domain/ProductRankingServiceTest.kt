package com.loopers.domain

import com.loopers.domain.ranking.ProductRankingRepository
import com.loopers.domain.ranking.ProductRankingService
import com.loopers.domain.ranking.RankingWeightRepository
import com.loopers.interfaces.consumer.MetricKafkaEvent
import com.loopers.interfaces.consumer.OrderItemEventDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate
import kotlin.test.Test

class ProductRankingServiceTest {

    private lateinit var rankingWeightRepository: RankingWeightRepository
    private lateinit var productRankingRepository: ProductRankingRepository
    private lateinit var productRankingService: ProductRankingService

    @BeforeEach
    fun setUp() {
        rankingWeightRepository = mockk()
        productRankingRepository = mockk(relaxed = true)
        productRankingService = ProductRankingService(rankingWeightRepository, productRankingRepository)
    }

    @Test
    fun `상품 조회 이벤트 시 기본 가중치로 점수 반영`() {
        every { rankingWeightRepository.findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(any(), any()) } returns null

        val event = MetricKafkaEvent(
            orderUUId = "order_123",
            eventType = EventType.PRODUCT_VIEW,
            productId = 100L
        )

        productRankingService.rank(event)

        verify {
            productRankingRepository.incrementScore(
                100L,
                1L * 0.05,
                LocalDate.now()
            )
        }
    }

    @Test
    fun `상품 구매 이벤트 시 price * qty * weight 반영`() {
        every { rankingWeightRepository.findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(any(), any()) } returns null

        val event = MetricKafkaEvent(
            orderUUId = "order_123",
            eventType = EventType.ORDERED,
            items = listOf(
                OrderItemEventDto(productId = 1L, price = 1000L, qty = 2),
                OrderItemEventDto(productId = 2L, price = 500L, qty = 3)
            )
        )

        productRankingService.rank(event)

        val expected = mapOf(
            1L to (1000L * 2) * 1.0,
            2L to (500L * 3) * 1.0
        )

        verify {
            productRankingRepository.incrementScoreAllAtOnce(expected, LocalDate.now())
        }
    }

    @Test
    fun `상품 좋아요 이벤트 시 기본 가중치 반영`() {
        every { rankingWeightRepository.findFirstByTargetTypeAndWeightTypeOrderByCreatedAtDesc(any(), any()) } returns null

        val event = MetricKafkaEvent(
            orderUUId = "order_123",
            eventType = EventType.PRODUCT_LIKED,
            productId = 200L
        )

        productRankingService.rank(event)

        verify {
            productRankingRepository.incrementScore(
                200L,
                1L * 0.25,
                LocalDate.now()
            )
        }
    }
}
