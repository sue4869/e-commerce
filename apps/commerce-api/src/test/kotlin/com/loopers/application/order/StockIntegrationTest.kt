package com.loopers.application.order

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.StockService
import com.loopers.domain.product.ProductRepository
import com.loopers.fixture.product.ProductFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import junit.framework.TestCase.assertTrue
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class StockIntegrationTest(
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val stockService: StockService
): IntegrationTestSupport() {

    private val log = KotlinLogging.logger {}


    @DisplayName("재고 동시성 테스트")
    @Nested
    inner class StockConcurrencyTest {

        @Test
        fun `요청 횟수보다 재고가 많으면 동시에 요청해도 모두 성공한다`() {
            // given
            val brand = brandRepository.save(BrandEntity(name = "브랜드"))
            val product = productRepository.save(
                ProductFixture.Normal.createByStock(brand.id, stock = 1000)
            )

            val threadCount = 10
            val qtyPerRequest = 10
            val successCounter = AtomicInteger(0)
            val initialStock = product.stock

            runConcurrent(threadCount) {
                val orderCommand = OrderCommand.Create(
                    userId = UUID.randomUUID().toString(),
                    items = listOf(
                        OrderCommand.Item(
                            productId = product.id!!,
                            qty = qtyPerRequest,
                            price = BigDecimal.valueOf(1000)
                        )
                    )
                )

                try {
                    stockService.changeStock(orderCommand, listOf(product.id!!))
                    successCounter.incrementAndGet()
                } catch (e: Exception) {
                    println("실패: ${e.message}")
                }
            }

            // then
            val updated = productRepository.findById(product.id!!)
            val expectedStock = initialStock - (threadCount * qtyPerRequest)

            assertEquals(threadCount, successCounter.get(), "모든 요청이 성공해야 함")
            log.info("threadCount: $threadCount, successCounter: ${successCounter.get()}")
            assertEquals(expectedStock, updated.stock, "재고가 정확히 차감되어야 함")
            log.info("expectedStock: $expectedStock, updated: ${updated.stock}")
        }

        @Test
        fun `동시 요청시 재고만큼 성공하고 나머지는 실패한다`() {
            // given
            val brand = brandRepository.save(BrandEntity(name = "브랜드"))
            val product = productRepository.save(
                ProductFixture.Normal.createByStock(brand.id, stock = 90)
            )

            val totalRequestCount = 10
            val successCount = AtomicInteger(0)
            val failCount = AtomicInteger(0)

            val qtyPerRequest = 10
            val orderCommand = OrderCommand.Create(
                userId = UUID.randomUUID().toString(),
                items = listOf(
                    OrderCommand.Item(
                        productId = product.id!!,
                        qty = qtyPerRequest,
                        price = BigDecimal.valueOf(1000)
                    )
                )
            )

            runConcurrent(totalRequestCount) {
                try {
                    stockService.changeStock(orderCommand, listOf(product.id!!))
                    successCount.incrementAndGet()
                } catch (e: CoreException) {
                    if (e.errorType == ErrorType.OUT_OF_STOCK) {
                        failCount.incrementAndGet()
                    } else {
                        throw e
                    }
                }
            }

            // then
            assertThat(successCount.get()).isEqualTo(9)
            assertThat(failCount.get()).isEqualTo(1)

            val result = productRepository.findById(product.id!!)
            assertThat(result.stock).isEqualTo(0)
        }

        @Test
        fun `동일한 product들을 다른 순서로 동시에 요청해도 데드락이 발생하지 않는다(정렬로 인한 데드락 방지 확인)`() {
            // given
            val brand1 = brandRepository.save(BrandEntity(name = "브랜드1"))
            val brand2 = brandRepository.save(BrandEntity(name = "브랜드2"))
            val product1 = productRepository.save(
                ProductFixture.Normal.createByStock(brand1.id, stock = 90)
            )
            val product2 = productRepository.save(
                ProductFixture.Normal.createByStock(brand2.id, stock = 90)
            )
            val qtyPerRequest = 10

            val orderCommand1 = OrderCommand.Create(
                userId = UUID.randomUUID().toString(),
                items = listOf(
                    OrderCommand.Item(
                        productId = product1.id!!,
                        qty = qtyPerRequest,
                        price = BigDecimal.valueOf(1000)
                    ),
                    OrderCommand.Item(
                        productId = product2.id!!,
                        qty = qtyPerRequest,
                        price = BigDecimal.valueOf(1000)
                    )
                )
            )

            val orderCommand2 = OrderCommand.Create(
                userId = UUID.randomUUID().toString(),
                items = listOf(
                    OrderCommand.Item(
                        productId = product2.id!!,
                        qty = qtyPerRequest,
                        price = BigDecimal.valueOf(1000)
                    ),
                    OrderCommand.Item(
                        productId = product1.id!!,
                        qty = qtyPerRequest,
                        price = BigDecimal.valueOf(1000)
                    )
                )
            )

            val executor = Executors.newFixedThreadPool(2)
            val readyLatch = CountDownLatch(2)  // 스레드 준비 확인
            val startLatch = CountDownLatch(1)  // 동시에 시작 신호
            val endLatch = CountDownLatch(2)    // 스레드 종료 대기

            // when
            executor.submit {
                readyLatch.countDown()
                startLatch.await()
                try {
                    stockService.changeStock(orderCommand1, listOf(product1.id!!, product2.id!!))
                } finally {
                    endLatch.countDown()
                }
            }

            executor.submit {
                readyLatch.countDown()
                startLatch.await()
                try {
                    stockService.changeStock(orderCommand2, listOf(product2.id!!, product1.id!!))
                } finally {
                    endLatch.countDown()
                }
            }

            // 두 스레드 준비될 때까지 대기
            readyLatch.await()
            // 동시에 시작
            startLatch.countDown()

            // then
            assertDoesNotThrow {
                assertTrue(endLatch.await(5, TimeUnit.SECONDS))
            }

            executor.shutdown()
        }
    }
}
