package com.loopers.domain.product

import com.loopers.domain.event.EventPublisher
import com.loopers.domain.event.dto.ProductDislikedEvent
import com.loopers.domain.event.dto.ProductLikedEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import kotlin.test.Test

@SpringBootTest
@RecordApplicationEvents
class ProductListenerTest {

    @Autowired
    lateinit var eventPublisher: EventPublisher

    lateinit var applicationEvents: ApplicationEvents

    @BeforeEach
    fun setup(applicationEvents: ApplicationEvents) {
        this.applicationEvents = applicationEvents
    }

    @Test
    fun `ProductLikeEvent 발행 확인`() {
        val productId = 1L

        eventPublisher.publish(ProductLikedEvent(productId))

        assertThat(applicationEvents.stream(ProductLikedEvent::class.java))
            .anyMatch { it.productId == productId }
    }

    @Test
    fun `ProductDislikeEvent 발행 확인`() {
        val productId = 1L

        eventPublisher.publish(ProductDislikedEvent(productId))

        assertThat(applicationEvents.stream(ProductDislikedEvent::class.java))
            .anyMatch { it.productId == productId }
    }
}



