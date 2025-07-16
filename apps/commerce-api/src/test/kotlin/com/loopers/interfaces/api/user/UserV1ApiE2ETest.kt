package com.loopers.interfaces.api.user

import com.loopers.fixture.user.UserFixture
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.TestSupport
import org.springframework.http.HttpHeaders
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

import kotlin.test.Test


class UserV1ApiE2ETest : TestSupport() {

    companion object {
        private val ENDPOINT_JOIN = "/api/v1/users"
    }

    @DisplayName("회원가입")
    @Nested
    inner class Join {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        fun create_User_after_return_userInfo() {
            //arrange
            val request = UserFixture.Normal.createSignUpRequest()

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_JOIN, HttpMethod.POST, HttpEntity(request), responseType)

            //assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue },
                { assertThat(response.body?.data?.userId).isEqualTo(request.userId)},
                { assertThat(response.body?.data?.birthDate).isEqualTo(request.birthDate)},
                { assertThat(response.body?.data?.email).isEqualTo(request.email) },
            )

        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        fun return_400_BadRequest_When_gender_null() {
            //arrange
            val request = """
                {
                    "userId": "1234",
                    "email": "email@naver.com",
                    "birthDate": "2025-07-14"
                }
            """.trimIndent()
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(request, headers)

            //act
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.Response.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_JOIN, HttpMethod.POST, httpEntity, responseType)


            //assert
            assertThat(response.statusCode.is4xxClientError).isTrue()
            println(response.body?.meta?.message)
        }
    }
}
