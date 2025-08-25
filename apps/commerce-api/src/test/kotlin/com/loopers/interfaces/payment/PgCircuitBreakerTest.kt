package com.loopers.interfaces.payment

import com.loopers.domain.payment.AfterPgProcessor
import com.loopers.domain.payment.CardPaymentProcessor
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PgClient
import com.loopers.domain.type.CardType
import com.loopers.domain.type.PaymentType
import com.loopers.support.IntegrationTestSupport
import com.ninjasquad.springmockk.MockkBean
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import kotlin.concurrent.atomics.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals

class PgCircuitBreakerTest(

): IntegrationTestSupport() {

    @Autowired
    lateinit var processor: CardPaymentProcessor

    @Autowired
    lateinit var circuitBreakerRegistry: CircuitBreakerRegistry

    @MockkBean
    lateinit var pgClient: PgClient

    @MockkBean
    lateinit var afterPgProcessor: AfterPgProcessor

    private val orderUUID = "order-123"
    private val userId = "user-123"
    private val payment = PaymentCommand.Payment(
        type = PaymentType.CARD,
        amount = 1000L,
        cardNo = "1234567890123456",
        cardType = CardType.SAMSUNG
    )

    private val cb: CircuitBreaker
        get() = circuitBreakerRegistry.circuitBreaker("pgClient")

    @BeforeEach
    fun setup() {
        cb.reset()
    }

    @Test
    fun `PG 연속 실패 시 CircuitBreaker OPEN`() {
        // given: pgClient 항상 실패
        every { pgClient.requestPayment(userId, any()) } throws RuntimeException("PG 실패")

        // when: CircuitBreaker OPEN 조건 충족
        repeat(7) {
            try {
                processor.charge(orderUUID, userId, payment)
            } catch (_: Exception) {}
        }

        // then: CircuitBreaker가 OPEN 상태인지 확인
        assertEquals(CircuitBreaker.State.OPEN, cb.state)
    }

    @Test
    fun `CircuitBreaker OPEN 후 HALF_OPEN에서 정상 호출 시 CLOSED로 전환`() {
        // given: 먼저 OPEN 상태로 만들기
        every { pgClient.requestPayment(userId, any()) } throws RuntimeException("PG 실패")

        repeat(4) {
            try {
                processor.charge(orderUUID, userId, payment)
            } catch (_: Exception) {}
        }
        assertEquals(CircuitBreaker.State.OPEN, cb.state)

        Thread.sleep(2100)
        every { pgClient.requestPayment(userId, any()) } returns mockk(relaxed = true)

        processor.charge(orderUUID, userId, payment)

        // then: CircuitBreaker가 CLOSED로 회복
        assertEquals(CircuitBreaker.State.CLOSED, cb.state)
    }

    @Test
    fun `정상 호출 반복 시 CircuitBreaker는 항상 CLOSED`() {
        every { pgClient.requestPayment(userId, any()) } returns mockk(relaxed = true)

        repeat(10) {
            processor.charge(orderUUID, userId, payment)
            assertEquals(CircuitBreaker.State.CLOSED, cb.state)
        }
    }

    @Test
    fun `임계치 이상 느린 호출 시 CircuitBreaker가 OPEN 상태로 전환된다`() {
        val slowThreshold = cb.circuitBreakerConfig.slowCallDurationThreshold.toMillis()

        every { pgClient.requestPayment(userId, any()) } answers {
            Thread.sleep(slowThreshold + 100) // 느린 호출
            mockk(relaxed = true)
        }

        repeat(cb.circuitBreakerConfig.minimumNumberOfCalls) {
            try {
                processor.charge(orderUUID, userId, payment)
            } catch (_: Exception) {}
        }

        assertEquals(CircuitBreaker.State.OPEN, cb.state)
    }

}
