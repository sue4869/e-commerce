package com.loopers.application.point

import com.loopers.domain.payment.PointPaymentProcessor
import com.loopers.domain.point.PointRepository
import com.loopers.domain.point.PointService
import com.loopers.fixture.point.PointFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import mu.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.math.BigDecimal
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

class PointIntegrationTest(
    private val pointFacade: PointFacade,
    private val pointService: PointService,
    private val pointRepository: PointRepository,
    private val paymentProcessor: PointPaymentProcessor
): IntegrationTestSupport() {

    private val log = KotlinLogging.logger {}

    @DisplayName("포인트 조회")
    @Nested
    inner class GetPoint {

        @Test
        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        fun return_point_when_user_id_exists() {
            //arrange
            val point = PointFixture.Normal.pointEntity()
            pointRepository.save(point)
            val testUserId = point.userId

            //act
            val result = pointRepository.findByUserId(testUserId)

            //assert
            assertAll(
                { assertThat(result?.userId).isEqualTo(testUserId)},
                { assertThat(result?.amount?.compareTo(point.amount)).isZero() }
            )
        }
    }

    @DisplayName("포인트 충전")
    @Nested
    inner class Charge {

        @Test
        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패하여 CoreException 발생.")
        fun return_point_when_user_id_exists() {
            //arrange
            val point = PointFixture.Normal.pointCommand()

            //act
            val result = assertThrows<CoreException> {
                pointFacade.charge(point)
            }

            //assert
            assertThat(result.errorType).isEqualTo(ErrorType.NOT_FOUND_USER_ID)
        }
    }

    @DisplayName("동시성 테스트")
    @Nested
    inner class ConcurrencyTest {

        @Test
        fun `포인트 차감 시 하나만 성공한다_나머지는 다 ObjectOptimisticLockingFailureException가 발생한다`() {
            val point = PointFixture.Normal.pointEntity(userId = "user1", amount = BigDecimal.valueOf(100000))
            pointRepository.save(point)

            val threadCount = 5
            val useAmount = BigDecimal(15)
            val errors = Collections.synchronizedList(mutableListOf<Exception>())
            val successCount = AtomicInteger(0)

            runConcurrent(threadCount) {
                try {
                    paymentProcessor.charge("user1", useAmount)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    errors.add(e)
                }
            }

            val remaining = pointRepository.findByUserId("user1")?.amount ?: BigDecimal.ZERO
            log.info("성공한 쓰레드 수: ${successCount.get()}")
            log.info("실패한 쓰레드 수: ${errors.size}")
            log.info("최종 잔액: $remaining")

            assertThat(successCount.get()).isEqualTo(1)
            assertThat(errors).anyMatch { it is ObjectOptimisticLockingFailureException }
            assertThat(remaining).isGreaterThanOrEqualTo(BigDecimal.ZERO)
        }

        @Test
        fun `포인트 충전 시 하나만 성공한다_나머지는 다 ObjectOptimisticLockingFailureException가 발생한다`() {
            val point = PointFixture.Normal.pointEntity(userId = "user1", amount = BigDecimal.valueOf(100000))
            pointRepository.save(point)
            val pointCommand = PointFixture.Normal.pointCommand(userId = "user1", amount = BigDecimal.valueOf(1))

            val threadCount = 5
            val errors = Collections.synchronizedList(mutableListOf<Exception>())
            val successCount = AtomicInteger(0)

            runConcurrent(threadCount) {
                try {
                    pointService.charge(pointCommand)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    errors.add(e)
                }
            }

            val remaining = pointRepository.findByUserId("user1")?.amount ?: BigDecimal.ZERO
            log.info("성공한 쓰레드 수: ${successCount.get()}")
            log.info("실패한 쓰레드 수: ${errors.size}")
            log.info("최종 잔액: $remaining")

            assertThat(successCount.get()).isEqualTo(1)
            assertThat(errors).anyMatch { it is ObjectOptimisticLockingFailureException }
        }
    }

    @Test
    fun `동시에 충전과 사용이 발생할 때 낙관적 락 충돌(ObjectOptimisticLockingFailureException)이 하나는 발생해야 한다`() {
        val userId = "user1"
        val initialAmount = BigDecimal.valueOf(100000)
        val point = PointFixture.Normal.pointEntity(userId = userId, amount = initialAmount)
        pointRepository.save(point)
        val pointCommand = PointFixture.Normal.pointCommand(userId = "user1", amount = BigDecimal.valueOf(1000))

        val threadCount = 2
        val useAmount = BigDecimal.valueOf(1500)

        val errors = Collections.synchronizedList(mutableListOf<Exception>())
        val chargeCount = AtomicInteger(0)
        val useCount = AtomicInteger(0)

        runConcurrent(threadCount) {
            try {
                if (Thread.currentThread().id % 2 == 0L) {
                    // 짝수 스레드는 충전
                    pointService.charge(pointCommand)
                    chargeCount.incrementAndGet()
                } else {
                    // 홀수 스레드는 사용
                    paymentProcessor.charge("user1", useAmount)
                    useCount.incrementAndGet()
                }
            } catch (e: Exception) {
                errors.add(e)
            }
        }

        val remaining = pointRepository.findByUserId(userId)?.amount ?: BigDecimal.ZERO
        log.info("충전 성공한 스레드 수: ${chargeCount.get()}")
        log.info("사용 성공한 스레드 수: ${useCount.get()}")
        log.info("실패한 스레드 수: ${errors.size}")
        log.info("최종 잔액: $remaining")

        // 충전 + 사용 성공 횟수는 threadCount보다 작거나 같아야 함(락 충돌로 일부 실패 가능)
        assertThat(chargeCount.get() + useCount.get()).isLessThanOrEqualTo(threadCount)

        // 낙관적 락 예외가 적어도 하나 이상 발생해야 함
        assertThat(errors).anyMatch { it is ObjectOptimisticLockingFailureException }

        // 최종 잔액은 음수가 될 수 없음
        assertThat(remaining).isGreaterThanOrEqualTo(BigDecimal.ZERO)
    }

}
