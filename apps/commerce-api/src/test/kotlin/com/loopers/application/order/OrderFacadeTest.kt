package com.loopers.application.order


import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderItemDto
import com.loopers.domain.order.OrderItemService
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.StockService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductHistoryDto
import com.loopers.domain.product.ProductHistoryService
import com.loopers.domain.type.OrderItemStatus
import org.mockito.kotlin.whenever

import com.loopers.domain.type.PaymentType
import com.loopers.fixture.product.ProductFixture
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@ExtendWith(MockitoExtension::class)
class OrderFacadeTest {

    @Mock
    private lateinit var productHistoryService: ProductHistoryService
    @Mock
    private lateinit var orderService: OrderService
    @Mock
    private lateinit var orderItemService: OrderItemService
    @Mock
    private lateinit var paymentService: PaymentService
    @Mock
    private lateinit var stockService: StockService

    @InjectMocks
    private lateinit var orderFacade: OrderFacade

    @DisplayName("주문 생성 성공 테스트")
    @Test
    fun `success_order`() {
        // given
        val userId = "user-123"
        val items = listOf(
            OrderCommand.Item(productId = 1L, price = BigDecimal(1000), qty = 2),
            OrderCommand.Item(productId = 2L, price = BigDecimal(2000), qty = 1),
        )
        val payments = listOf(
            PaymentCommand.Payment(type = PaymentType.POINT, amount = BigDecimal.valueOf(123))
        )
        val orderCommand = OrderCommand.Create(userId = userId, items = items)
        val paymentCommand = PaymentCommand.Create(payments = payments)

        val productHistoryDtos = listOf(
            ProductHistoryDto(
                productId = 1L, productHistoryId = 101L, productName = "Product 1", brandId = 10L, stock = 100, likeCount = 5
            ),
            ProductHistoryDto(
                productId = 2L, productHistoryId = 102L, productName = "Product 2", brandId = 10L, stock = 50, likeCount = 3
            ),
        )
        val orderId = 555L
        val orderItemDtos = listOf(
            OrderItemDto(1L, orderId, 101L, BigDecimal(1000), BigDecimal(2000), 2, OrderItemStatus.ORDERED),
            OrderItemDto(2L, orderId, 102L, BigDecimal(2000), BigDecimal(2000), 1, OrderItemStatus.ORDERED),
        )

        // stubbing
        whenever(productHistoryService.getProductsForOrder(listOf(1L, 2L))).thenReturn(productHistoryDtos)
        whenever(orderService.create(orderCommand)).thenReturn(orderId)
        whenever(orderItemService.create(items, orderId, productHistoryDtos)).thenReturn(orderItemDtos)
        doNothing().whenever(paymentService).charge(userId, orderItemDtos.sumOf { it.totalPrice }, paymentCommand)
        doNothing().whenever(stockService).changeStock(orderCommand, listOf(1L, 2L))

        // when
        orderFacade.create(orderCommand, paymentCommand)

        // then
        verify(productHistoryService).getProductsForOrder(listOf(1L, 2L))
        verify(orderService).create(orderCommand)
        verify(orderItemService).create(items, orderId, productHistoryDtos)
        verify(paymentService).charge(userId, orderItemDtos.sumOf { it.totalPrice }, paymentCommand)
        verify(stockService).changeStock(orderCommand, listOf(1L, 2L))
    }
}

