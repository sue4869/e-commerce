package com.loopers.domain.point

import com.loopers.fixture.point.PointFixture
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PointModelTest {

    @DisplayName("포인트 쌓기")
    @Nested
    inner class Charge {

        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패하여 CoreException이 발생한다.")
        @Test
        fun return_fail_when_charging_negative_amount() {
            //arrange
            val userId = "testId"
            val request = PointFixture.Normal.chargeRequest(
                amount = -2L,
            )

            //act
            val result = assertThrows<CoreException> {
                request.toCommand(userId)
            }

            //assert
            assertThat(result.errorType).isEqualTo(ErrorType.CHARGE_AMOUNT_MUST_BE_POSITIVE)
        }
    }
}
