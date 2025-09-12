package com.loopers.domain.product

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import kotlin.test.Test

class ProductRankServiceTest {

    private lateinit var productRankingRepository: ProductRankingRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var productRankService: ProductRankService

    @BeforeEach
    fun setUp() {
        productRankingRepository = mockk()
        productRepository = mockk()
        productRankService = ProductRankService(productRankingRepository, productRepository)
    }

    @Test
    fun `getRankingDaily returns Page of ProductRankDto`() {
        val date = LocalDate.of(2025, 9, 13)
        val pageable = PageRequest.of(0, 2)
        val command = ProductCommand.RankingDaily(date = date, pageable = pageable)

        // Redis에서 반환될 데이터 mocking
        every { productRankingRepository.getRankingList(date, pageable) } returns listOf(
            1L to 10.0,
            2L to 8.0
        )

        // DB에서 반환될 ProductEntity mocking
        every { productRepository.findByIdIn(listOf(1L, 2L)) } returns listOf(
            ProductEntity( name = "Product A", brandId = 100, price = 5000, stock = 10),
            ProductEntity( name = "Product B", brandId = 101, price = 3000, stock = 5)
        )

        // 총 요소 수 mocking
        every { productRankingRepository.getTotalCount(date) } returns 2L

        val page: Page<ProductRankDto> = productRankService.getRankingDaily(command)

        assertEquals(2, page.content.size)
        assertEquals(1, page.content[0].rank)
        assertEquals(10.0, page.content[0].score)

        assertEquals(2, page.content[1].rank)
        assertEquals(8.0, page.content[1].score)
    }

    @Test
    fun `getRanking returns correct list`() {
        val date = LocalDate.of(2025, 9, 13)
        val pageable = PageRequest.of(0, 2)
        val command = ProductCommand.RankingDaily(date = date, pageable = pageable)

        every { productRankingRepository.getRankingList(date, pageable) } returns listOf(
            3L to 15.0
        )

        every { productRepository.findByIdIn(listOf(3L)) } returns listOf(
            ProductEntity(name = "Product C", brandId = 102, price = 7000, stock = 3)
        )

        val result: List<ProductRankDto> = productRankService.getRanking(command)

        assertEquals(1, result.size)
        assertEquals(1, result[0].rank)
        assertEquals(15.0, result[0].score)
    }
}
