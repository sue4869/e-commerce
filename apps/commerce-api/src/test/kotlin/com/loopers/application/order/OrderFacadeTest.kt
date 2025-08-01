package com.loopers.application.order


import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderItemDto
import com.loopers.domain.order.OrderItemService
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.StockService
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.product.ProductHistoryDto
import com.loopers.domain.product.ProductHistoryService
import com.loopers.domain.type.OrderItemStatus
import org.mockito.kotlin.whenever

import com.loopers.domain.type.PaymentType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import java.math.BigDecimal

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

    @Test
    fun `create should process order successfully`() {
        // given
        val userId = "user-123"
        val items = listOf(
            OrderCommand.Item(productId = 1L, price = BigDecimal(1000), qty = 2),
            OrderCommand.Item(productId = 2L, price = BigDecimal(2000), qty = 1),
        )
        val paymentType = PaymentType.POINT
        val command = OrderCommand.Create(userId = userId, items = items, paymentType = paymentType)

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
        whenever(orderService.create(command)).thenReturn(orderId)
        whenever(orderItemService.create(items, orderId, productHistoryDtos)).thenReturn(orderItemDtos)
        doNothing().whenever(paymentService).charge(userId, paymentType, orderItemDtos)
        doNothing().whenever(stockService).changeStock(command, listOf(1L, 2L))

        // when
        orderFacade.create(command)

        // then
        verify(productHistoryService).getProductsForOrder(listOf(1L, 2L))
        verify(orderService).create(command)
        verify(orderItemService).create(items, orderId, productHistoryDtos)
        verify(paymentService).charge(userId, paymentType, orderItemDtos)
        verify(stockService).changeStock(command, listOf(1L, 2L))
    }
}

