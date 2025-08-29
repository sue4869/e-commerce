package com.loopers.domain.order

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.PaidCompleteEvent
import com.loopers.domain.event.dto.PaidFailEvent
import com.loopers.domain.event.dto.StockFailedEvent
import com.loopers.domain.type.OrderStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import kotlin.test.Test

@SpringBootTest
@RecordApplicationEvents
class OrderEventListenerTest {

    @Autowired
    lateinit var eventPublisher: EventPublisher

    lateinit var applicationEvents: ApplicationEvents

    @BeforeEach
    fun setup(applicationEvents: ApplicationEvents) {
        this.applicationEvents = applicationEvents
    }

    @Test
    fun `PaidCompleteEvent 발행 확인`() {
        val orderUUId = "orderUUId"

        eventPublisher.publish(PaidCompleteEvent(orderUUId, OrderStatus.PAID))

        assertThat(applicationEvents.stream(PaidCompleteEvent::class.java))
            .anyMatch { it.orderUUId == orderUUId }
    }

    @Test
    fun `PaidFailEvent 발행 확인`() {
        val orderUUId = "orderUUId"

        eventPublisher.publish(PaidFailEvent(orderUUId, OrderStatus.PAID))

        assertThat(applicationEvents.stream(PaidFailEvent::class.java))
            .anyMatch { it.orderUUId == orderUUId }
    }

}
