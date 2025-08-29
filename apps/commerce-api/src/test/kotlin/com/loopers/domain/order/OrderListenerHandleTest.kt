package com.loopers.domain.order

import com.loopers.domain.coupon.UserToCouponService
import com.loopers.domain.event.dto.PaidCompletedEvent
import com.loopers.domain.event.listener.OrderListener
import com.loopers.domain.type.IssuedStatus
import com.loopers.domain.type.OrderStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class OrderListenerHandleTest {

    @InjectMocks
    private lateinit var eventListener: OrderListener

    @Mock
    private lateinit var orderService: OrderService

    @Mock
    private lateinit var stockService: StockService

    @Mock
    private lateinit var userToCouponService: UserToCouponService

    @Test
    @DisplayName("결제 완료 이벤트 수신 시 - 주문 상태 업데이트, 재고 차감, 쿠폰 사용 처리")
    fun `결제 완료 이벤트가 정상 처리된다`() {
        // given
        val orderId = 1L
        val couponId = 100L
        val userId = "user-abc"
        val uuid = "order-uuid-123"

        val orderDto = OrderDto(
            orderId = orderId,
            couponId = couponId,
            uuid = uuid,
            userId = userId,
            totalPrice = 5000L,
            status = OrderStatus.PAID,
            canceledPrice = null,
            submittedPrice = 5000L
        )

        whenever(orderService.updateStatus(uuid, OrderStatus.PAID)).thenReturn(orderDto)

        val event = PaidCompletedEvent(orderUUId = uuid, status = OrderStatus.PAID)

        // when
        eventListener.handleAfterPaid(event)

        // then
        verify(orderService).updateStatus(uuid, OrderStatus.PAID)
        verify(stockService).reduceStock(uuid, OrderStatus.PAID)
        verify(userToCouponService).updateStatus(userId, couponId, IssuedStatus.USED)
    }

    @Test
    @DisplayName("쿠폰이 없는 경우 - 쿠폰 서비스는 호출되지 않는다")
    fun `쿠폰 없는 주문 처리`() {
        // given
        val orderId = 2L
        val userId = "user-xyz"
        val uuid = "order-uuid-456"

        val orderDto = OrderDto(
            orderId = orderId,
            couponId = null,
            uuid = uuid,
            userId = userId,
            totalPrice = 8000L,
            status = OrderStatus.PAID,
            canceledPrice = null,
            submittedPrice = 8000L
        )

        whenever(orderService.updateStatus(uuid, OrderStatus.PAID)).thenReturn(orderDto)

        val event = PaidCompletedEvent(orderUUId = uuid, status = OrderStatus.PAID)

        // when
        eventListener.handleAfterPaid(event)

        // then
        verify(orderService).updateStatus(uuid, OrderStatus.PAID)
        verify(stockService).reduceStock(uuid, OrderStatus.PAID)
        verify(userToCouponService, never()).updateStatus(any(), any(), any())
    }
}

