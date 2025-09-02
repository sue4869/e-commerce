package com.loopers.domain.payment

import com.loopers.application.payment.PaymentFacade
import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.PaidCompletedEvent
import com.loopers.domain.event.dto.PaidFailedEvent
import com.loopers.domain.type.CardType
import com.loopers.domain.type.OrderStatus
import com.loopers.domain.type.PaymentStatus
import kotlin.test.Test
import io.mockk.mockk
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat


class PaymentEventTest {

    private val afterPgProcessor: AfterPgProcessor = mockk()
    private val eventPublisher: EventPublisher = mockk(relaxed = true)

    private val paymentFacade = PaymentFacade(afterPgProcessor, eventPublisher)


    private fun createCommand(orderId: String) = PgAfterCommand(
        transactionKey = "tx-001",
        orderId = orderId,
        cardType = CardType.SAMSUNG,
        cardNo = "1111-2222-3333-4444",
        amount = 1000L,
        reason = null
    )

    @Test
    fun `executeAfterPg 성공 시 OrderStatusChangeDto 이벤트가 발행된다`() {
        // given
        val command = createCommand(orderId = "order-123")
        every { afterPgProcessor.updatePaymentStatus(command) } returns PaymentStatus.SUCCESS

        // when
        paymentFacade.executeAfterPg(command)

        // then
        val slot = slot<PaidCompletedEvent>()
        verify(exactly = 1) { eventPublisher.publish(capture(slot)) }

        assertThat(slot.captured.orderUUId).isEqualTo("order-123")
        assertThat(slot.captured.status).isEqualTo(OrderStatus.PAID)
    }

    @Test
    fun `실패 시 이벤트에 CANCELLED 상태가 담겨 발행된다`() {
        // given
        val command = createCommand("order-456")
        every { afterPgProcessor.updatePaymentStatus(command) } returns PaymentStatus.FAILED

        // when
        paymentFacade.executeAfterPg(command)

        // then
        val slot = slot<PaidFailedEvent>()
        verify(exactly = 1) { eventPublisher.publish(capture(slot)) }

        assertThat(slot.captured.orderUUId).isEqualTo("order-456")
        assertThat(slot.captured.status).isEqualTo(OrderStatus.CANCELLED)
    }


}
