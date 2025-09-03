package com.loopers.application.order

import com.loopers.domain.coupon.UserToCouponService
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.OrderDto
import com.loopers.domain.order.OrderItemDto
import com.loopers.domain.order.OrderItemService
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.type.OrderItemStatus
import com.loopers.domain.type.OrderStatus
import org.mockito.kotlin.whenever

import com.loopers.domain.type.PaymentType
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class OrderFacadeTest {

    @Mock
    private lateinit var orderService: OrderService

    @Mock
    private lateinit var orderItemService: OrderItemService

    @Mock
    private lateinit var paymentService: PaymentService

    @Mock
    private lateinit var userToCouponService: UserToCouponService

    @InjectMocks
    private lateinit var orderFacade: OrderFacade

    @DisplayName("주문 생성 성공 테스트")
    @Test
    fun `주문 성공 - 주문 생성 후 결제 요청까지`() {
        // given
        val userId = "user-123"
        val orderUUID = "uuid-001"
        val orderId = 100L
        val totalPrice = 5000L

        val orderCommand = OrderCommand.Create(
            userId = userId,
            items = listOf(
                OrderCommand.Item(productId = 1L, qty = 2, price = 1000L),
                OrderCommand.Item(productId = 2L, qty = 1, price = 3000L),
            ),
        )

        val paymentCommand = PaymentCommand.Create(
            payments = listOf(PaymentCommand.Payment(type = PaymentType.POINT, amount = totalPrice)),
            finalAmount = totalPrice,
            originAmount = totalPrice,
        )

        val orderDto = OrderDto(orderId = orderId, uuid = orderUUID, userId = userId, totalPrice = totalPrice, status = OrderStatus.ORDERED, canceledPrice = null, submittedPrice = totalPrice)
        val orderItemsDto = listOf(
            OrderItemDto(id = 1L, orderId = orderId, productId = 1L, unitPrice = 1000L, totalPrice = 2000L, qty = 2, status = OrderItemStatus.ORDERED),
            OrderItemDto(id = 2L, orderId = orderId, productId = 2L, unitPrice = 3000L, totalPrice = 3000L, qty = 1, status = OrderItemStatus.ORDERED),
        )

        // stubbing
        whenever(orderService.create(orderCommand)).thenReturn(orderDto)
        whenever(orderItemService.create(orderCommand.items, orderDto.orderId)).thenReturn(orderItemsDto)
        doNothing().`when`(paymentService).charge(orderUUID, userId, orderItemsDto.sumOf { it.totalPrice }, paymentCommand)
        whenever(orderService.updateStatus(orderUUID, OrderStatus.PAYMENT_PENDING))
            .thenReturn(orderDto)

        // when
        orderFacade.create(orderCommand, paymentCommand)

        // then
        verify(orderService, times(1)).create(orderCommand)
        verify(orderItemService, times(1)).create(orderCommand.items, orderDto.orderId)
        verify(paymentService, times(1)).charge(orderUUID, userId, orderItemsDto.sumOf { it.totalPrice }, paymentCommand)
        verify(orderService, times(1)).updateStatus(orderUUID, OrderStatus.PAYMENT_PENDING)
    }
}
