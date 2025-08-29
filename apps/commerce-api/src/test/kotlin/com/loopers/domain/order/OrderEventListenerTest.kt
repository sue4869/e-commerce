package com.loopers.domain.order

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.PaidCompletedEvent
import com.loopers.domain.event.dto.PaidFailedEvent
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

        eventPublisher.publish(PaidCompletedEvent(orderUUId, OrderStatus.PAID))

        assertThat(applicationEvents.stream(PaidCompletedEvent::class.java))
            .anyMatch { it.orderUUId == orderUUId }
    }

    @Test
    fun `PaidFailEvent 발행 확인`() {
        val orderUUId = "orderUUId"

        eventPublisher.publish(PaidFailedEvent(orderUUId, OrderStatus.PAID))

        assertThat(applicationEvents.stream(PaidFailedEvent::class.java))
            .anyMatch { it.orderUUId == orderUUId }
    }

}
