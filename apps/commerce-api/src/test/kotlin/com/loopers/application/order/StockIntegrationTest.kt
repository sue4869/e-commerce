package com.loopers.application.order

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderRepository
import com.loopers.domain.order.OrderItemRepository
import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.order.StockService
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.type.OrderStatus
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
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val stockService: StockService,
) : IntegrationTestSupport() {

    private val log = KotlinLogging.logger {}

    @DisplayName("재고 동시성 테스트")
    @Nested
    inner class StockConcurrencyTest {

        @Test
        fun `요청 횟수보다 재고가 많으면 동시에 요청해도 모두 성공한다`() {
            // given
            val brand = brandRepository.save(BrandEntity(name = "브랜드"))
            val product = productRepository.save(
                ProductFixture.Normal.createByStock(brand.id, stock = 1000),
            )

            val order = orderRepository.save(OrderEntity.of(userId = "user-1", totalPrice = 10000))
            val orderUUId = order.uuid
            orderItemRepository.saveAll(
                listOf(
                OrderItemEntity(
                    orderId = order.id!!,
                    productId = product.id!!,
                    unitPrice = 1000L,
                    totalPrice = 1000L * 10,
                    qty = 10,
                ),
                ),
            )
            val orderId = order.id

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
                            price = 1000L,
                        ),
                    ),
                )

                try {
                    stockService.reduceStock(orderUUId, OrderStatus.PAID)
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
                ProductFixture.Normal.createByStock(brand.id, stock = 90),
            )
            val order = orderRepository.save(OrderEntity.of(userId = "user-1", totalPrice = 10000))
            orderItemRepository.saveAll(
                listOf(
                OrderItemEntity(
                    orderId = order.id,
                    productId = product.id,
                    unitPrice = 1000L,
                    totalPrice = 1000L * 10,
                    qty = 10,
                ),
            ),
            )
            val orderUUId = order.uuid
            val totalRequestCount = 10
            val successCount = AtomicInteger(0)
            val failCount = AtomicInteger(0)

            runConcurrent(totalRequestCount) {
                try {
                    stockService.reduceStock(orderUUId, OrderStatus.PAID)
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
        fun `동일한 product들을 다른 순서로 동시에 요청해도 데드락이 발생하지 않는다`() {
            // given
            val brand1 = brandRepository.save(BrandEntity(name = "브랜드1"))
            val brand2 = brandRepository.save(BrandEntity(name = "브랜드2"))
            val product1 = productRepository.save(ProductFixture.Normal.createByStock(brand1.id, stock = 90))
            val product2 = productRepository.save(ProductFixture.Normal.createByStock(brand2.id, stock = 90))
            val qtyPerRequest = 10

            // 주문1 생성
            val order1 = orderRepository.save(OrderEntity.of("user-1", totalPrice = 20000))
            orderItemRepository.saveAll(
                listOf(
                OrderItemEntity(orderId = order1.id!!, productId = product1.id!!, unitPrice = 1000, totalPrice = 10000, qty = 10),
                OrderItemEntity(orderId = order1.id!!, productId = product2.id!!, unitPrice = 1000, totalPrice = 10000, qty = 10),
            ),
            )

            // 주문2 생성 (상품 순서 반대)
            val order2 = orderRepository.save(OrderEntity.of("user-2", totalPrice = 20000))
            orderItemRepository.saveAll(
                listOf(
                OrderItemEntity(orderId = order2.id!!, productId = product2.id!!, unitPrice = 1000, totalPrice = 10000, qty = 10),
                OrderItemEntity(orderId = order2.id!!, productId = product1.id!!, unitPrice = 1000, totalPrice = 10000, qty = 10),
            ),
            )

            val executor = Executors.newFixedThreadPool(2)
            val readyLatch = CountDownLatch(2)
            val startLatch = CountDownLatch(1)
            val endLatch = CountDownLatch(2)

            // when
            executor.submit {
                readyLatch.countDown()
                startLatch.await()
                try {
                    stockService.reduceStock(order1.uuid, OrderStatus.PAID)
                } finally {
                    endLatch.countDown()
                }
            }

            executor.submit {
                readyLatch.countDown()
                startLatch.await()
                try {
                    stockService.reduceStock(order2.uuid, OrderStatus.PAID)
                } finally {
                    endLatch.countDown()
                }
            }

            // 두 스레드 준비될 때까지 대기
            readyLatch.await()
            // 동시에 시작
            startLatch.countDown()

            // then
            assertDoesNotThrow { assertTrue(endLatch.await(5, TimeUnit.SECONDS)) }

            executor.shutdown()
            executor.awaitTermination(5, TimeUnit.SECONDS)
        }
    }
}
