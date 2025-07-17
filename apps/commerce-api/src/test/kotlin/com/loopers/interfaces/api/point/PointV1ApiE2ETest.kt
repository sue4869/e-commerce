package com.loopers.interfaces.api.point

import com.loopers.fixture.point.PointFixture
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.TestSupport
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import kotlin.test.Test

class PointV1ApiE2ETest : TestSupport() {

    companion object {
        private val ENDPOINT_POINT = "/api/v1/point"
    }

    @DisplayName("포인트 조회")
    @Nested
    inner class GetPoint {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        fun return_current_point_when_get() {
            //arrange
            val pointInfo = PointFixture.Normal
            //act
            val headers = HttpHeaders().apply { add("X-USER-ID", pointInfo.userId) }
            val httpEntity = HttpEntity(null, headers)
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.Response.PointResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_POINT, HttpMethod.GET, httpEntity, responseType)

            //assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue },
                { assertThat(response.body?.data?.userId).isEqualTo(pointInfo.userId)},
                { assertThat(response.body?.data?.amount).isEqualTo(pointInfo.amount)}
            )
        }
    }
}
