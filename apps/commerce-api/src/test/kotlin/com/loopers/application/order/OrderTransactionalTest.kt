package com.loopers.application.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.StockService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.type.PaymentType
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.aop.TransactionTraceHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.Test

@Transactional
class OrderTransactionalTest(
    private val orderFacade: OrderFacade,
): IntegrationTestSupport() {

    @MockitoBean
    private lateinit var stockService: StockService

    @MockitoBean
    private lateinit var paymentService: PaymentService

    @Test
    fun `order 생성 중 재고차감 서비스에서 예외 발생 시 전체 트랜잭션 롤백확인`() {
        // given
        val userId = "user1"
        val items = listOf(
            OrderCommand.Item(productId = 1L, price = BigDecimal(1000), qty = 2),
            OrderCommand.Item(productId = 2L, price = BigDecimal(2000), qty = 1),
        )
        val payments = listOf(
            PaymentCommand.Payment(type = PaymentType.POINT, amount = BigDecimal.valueOf(123))
        )
        val orderCommand = OrderCommand.Create(userId = userId, items = items)
        val paymentCommand = PaymentCommand.Create(payments = payments)

        doThrow(RuntimeException("재고 차감 실패")).whenever(stockService).changeStock(any(), any())

        // when
        assertThrows<RuntimeException> {
            orderFacade.create(orderCommand, paymentCommand)
        }

        // then
        val info = TransactionTraceHolder.get()
        assertThat(info).isNotNull()
        assertThat(info.rollbackOnly).isTrue()
        assertThat(info.exceptionOccurred).isTrue()
    }

    @Test
    fun `order 생성 중 포인트차감 서비스에서 예외 발생 시 전체 트랜잭션 롤백확인`() {
        // given
        val userId = "user1"
        val items = listOf(
            OrderCommand.Item(productId = 1L, price = BigDecimal(1000), qty = 2),
            OrderCommand.Item(productId = 2L, price = BigDecimal(2000), qty = 1),
        )
        val payments = listOf(
            PaymentCommand.Payment(type = PaymentType.POINT, amount = BigDecimal.valueOf(123))
        )
        val orderCommand = OrderCommand.Create(userId = userId, items = items)
        val paymentCommand = PaymentCommand.Create(payments = payments)

        doThrow(RuntimeException("포인트 차감 실패")).whenever(paymentService).charge(any(), any(), any())

        // when
        assertThrows<RuntimeException> {
            orderFacade.create(orderCommand, paymentCommand)
        }

        // then
        val info = TransactionTraceHolder.get()
        assertThat(info).isNotNull()
        assertThat(info.rollbackOnly).isTrue()
        assertThat(info.exceptionOccurred).isTrue()
    }
}
