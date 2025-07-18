package com.loopers.application.point

import com.loopers.domain.point.PointRepository
import com.loopers.fixture.point.PointFixture
import com.loopers.support.IntegrationTestSupport
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class PointIntegrationTest(
    private val pointFacade: PointFacade,
    private val pointRepository: PointRepository
): IntegrationTestSupport() {

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
}
