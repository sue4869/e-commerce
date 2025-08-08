package com.loopers.domain.order

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.PointPaymentProcessor
import com.loopers.domain.point.PointEntity
import com.loopers.domain.point.PointRepository
import com.loopers.domain.type.OrderItemStatus
import com.loopers.domain.type.PaymentType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.math.BigDecimal
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class PaymentServiceTest {

    @Mock
    private lateinit var paymentService: PaymentService

    @Mock
    private lateinit var pointRepository: PointRepository

    @InjectMocks
    private lateinit var pointPaymentProcessor: PointPaymentProcessor

    @DisplayName("결제 서비스 정상 호출")
    @Test
    fun `success_payment`() {
        // given
        val userId = "user-123"
        val paymentCommand = PaymentCommand.Create(listOf(
            PaymentCommand.Payment(type = PaymentType.POINT, amount = BigDecimal.valueOf(2000))
        ))
        val orderItems = listOf(
            OrderItemDto(
                id = 1L,
                orderId = 100L,
                productHistoryId = 200L,
                unitPrice = BigDecimal("1000.00"),
                totalPrice = BigDecimal("2000.00"),
                qty = 2,
                status = OrderItemStatus.ORDERED
            ),
            OrderItemDto(
                id = 2L,
                orderId = 100L,
                productHistoryId = 201L,
                unitPrice = BigDecimal("1500.00"),
                totalPrice = BigDecimal("1500.00"),
                qty = 1,
                status = OrderItemStatus.ORDERED
            )
        )

        // stub
        doNothing().`when`(paymentService).charge(userId, orderItems.sumOf { it.totalPrice }, paymentCommand)

        // when
        paymentService.charge(userId, orderItems.sumOf { it.totalPrice }, paymentCommand)

        // then
        verify(paymentService, times(1)).charge(userId, orderItems.sumOf { it.totalPrice }, paymentCommand)
    }

    @DisplayName("포인트 부족시 예외가 발생한다.(NOT_ENOUGH_POINTS)")
    @Test
    fun return_fail_when_point_not_enough() {

        // given
        val userId = "user-123"
        val point = PointEntity(amount = BigDecimal("1000"), userId = userId)
        val totalPrice = BigDecimal("2000")

        // when & then
        val exception = assertThrows<CoreException> {
            point.use(totalPrice)
        }

        assertThat(exception.errorType).isEqualTo(ErrorType.NOT_ENOUGH_POINTS)
        assertThat(exception.message).contains("포인트가 부족합니다")
    }

    @DisplayName("포인트 차감 후 저장된다")
    @Test
    fun charge_points_and_save() {
        // given
        val userId = "user-123"
        val point = PointEntity(amount = BigDecimal("5000"), userId = userId)
        val totalPrice = BigDecimal("2000")

        // when
        pointPaymentProcessor.updatePoint(point, totalPrice)

        // then
        assertThat(point.amount).isEqualTo(BigDecimal("3000"))
        verify(pointRepository).save(point)
    }
}
