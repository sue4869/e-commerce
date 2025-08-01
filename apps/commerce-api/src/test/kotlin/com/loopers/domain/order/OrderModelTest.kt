package com.loopers.domain.order

import com.loopers.fixture.order.OrderFixture
import com.loopers.interfaces.api.order.OrderV1Models
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderModelTest {

    @DisplayName("주문")
    @Nested
    inner class Order {

        @DisplayName("0 이하의 정수로 qty로 들어올 경우 실패하여 CoreException가 발생한다.")
        @Test
        fun return_fail_when_qty_negative() {
            val userId = "testId"
            val request = OrderFixture.InvalidQty.create()

            val result = assertThrows<CoreException> {
                request.toCommand(userId)
            }

            assertThat(result.errorType).isEqualTo(ErrorType.QTY_MUST_BE_POSITIVE)
        }

        @DisplayName("0 이하의 정수로 price로 들어올 경우 실패하여 CoreExceptio가 발생한다.")
        @Test
        fun return_fail_when_price_negative() {
            val userId = "testId"
            val request = OrderFixture.InvalidPrice.create()

            val result = assertThrows<CoreException> {
                request.toCommand(userId)
            }

            assertThat(result.errorType).isEqualTo(ErrorType.PRICE_MUST_BE_POSITIVE)
        }
    }
}
