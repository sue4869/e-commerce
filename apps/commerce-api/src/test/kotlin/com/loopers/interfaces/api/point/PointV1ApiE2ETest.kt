package com.loopers.interfaces.api.point

import com.loopers.domain.point.PointRepository
import com.loopers.domain.user.UserService
import com.loopers.fixture.point.PointFixture
import com.loopers.fixture.user.UserFixture
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.E2ETestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import kotlin.test.Test

class PointV1ApiE2ETest(
    private val userService: UserService,
    private val pointRepository: PointRepository,
) : E2ETestSupport() {

    companion object {
        private val ENDPOINT_GET_POINT = "/api/v1/points"
        private val ENDPOINT_CHARGE = "/api/v1/points/charge"
    }

    @DisplayName("포인트 조회")
    @Nested
    inner class GetPoint {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        fun return_current_point_when_get() {
            //arrange
            val point = PointFixture.Normal.pointEntity()
            pointRepository.save(point)
            val headers = headersWithUserId(point.userId)
            val httpEntity = HttpEntity(null, headers)

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Models.Response.Get>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_GET_POINT, HttpMethod.GET, httpEntity, responseType)

            //assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue },
                { assertThat(response.body?.data?.userId).isEqualTo(point.userId)},
                { assertThat(response.body?.data?.amount?.compareTo(point.amount)).isZero()}
            )
        }

        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        fun return_400_when_xuser_id_is_not_exist() {
            //arrange
            val httpEntity = HttpEntity.EMPTY

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Models.Response.Get>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_GET_POINT, HttpMethod.GET, httpEntity, responseType)

            //assert
            assertThat(response.statusCode.is4xxClientError).isTrue()
        }
    }

    @DisplayName("포인트 충전")
    @Nested
    inner class Charge {
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        fun return_total_amount_after_charge() {
            //arrange
            val user = UserFixture.Normal.createUserCommand()
            userService.create(user)
            val point = PointFixture.Normal.pointEntity()
            pointRepository.save(point)
            val headers = headersWithUserId( point.userId)
            val chargeRequest = PointFixture.Normal.chargeRequest(
                amount = 1000L
            )
            val httpEntity = HttpEntity(chargeRequest, headers)
            val totalAmount = point.amount.plus(chargeRequest.amount)

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Models.Response.Charge>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_CHARGE, HttpMethod.POST, httpEntity, responseType)

            //assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue },
                { assertThat(response.body?.data?.amount?.compareTo(totalAmount)).isZero()}
            )
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        fun return_404_when_unexisted_user() {
            //arrange
            val headers = headersWithUserId("test0")
            val chargeRequest = PointFixture.Normal.chargeRequest(
                amount = 1000L
            )
            val httpEntity = HttpEntity(chargeRequest, headers)

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Models.Response.Charge>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_CHARGE, HttpMethod.POST, httpEntity, responseType)

            //assert
            assertThat(response.statusCode.is4xxClientError).isTrue()
        }
    }
}
