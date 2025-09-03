package com.loopers.domain.order

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.StockFailedEvent
import com.loopers.domain.product.ProductEntity
import org.mockito.kotlin.capture
import com.loopers.domain.product.ProductRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.mockito.kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test
import com.loopers.domain.type.OrderStatus
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class StockServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var orderItemRepository: OrderItemRepository

    @Mock
    lateinit var eventPublisher: EventPublisher

    @InjectMocks
    lateinit var stockService: StockService

    @Test
    @DisplayName("재고가 충분하면 정상적으로 차감된다")
    fun `update_stock`() {
        // given
        val product = ProductEntity(
            name = "상품",
            brandId = 1L,
            price = 1000L,
            stock = 10,
        )

        // when
        stockService.updateStock(product, 3)

        // then
        assertThat(product.stock).isEqualTo(7)
    }

    @Test
    @DisplayName("재고가 부족하면 CoreException이 발생한다")
    fun `validateStock throws exception when stock is insufficient`() {
        val exception = assertThrows<CoreException> {
            stockService.validateStock(stock = 3, qty = 5)
        }

        assert(exception.errorType == ErrorType.OUT_OF_STOCK)
    }

    @Test
    fun `재고 차감 중 예외 발생 시 StockFailedEvent 발행`() {
        val orderUUId = "order-123"

        whenever(orderItemRepository.findByOrderUUId(orderUUId)).thenReturn(emptyList())

        assertThrows<CoreException> {
            stockService.reduceStock(orderUUId, OrderStatus.PAID)
        }

        val captor = argumentCaptor<StockFailedEvent>()
        verify(eventPublisher).publish(captor.capture())

        assertThat(captor.firstValue.orderUUId).isEqualTo(orderUUId)
        assertThat(captor.firstValue.exception).isInstanceOf(CoreException::class.java)
    }
}
